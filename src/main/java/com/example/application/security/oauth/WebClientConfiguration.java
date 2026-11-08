package com.example.application.security.oauth;

import com.example.application.data.model.ClientConfig;
import com.example.application.service.BucketService;
import com.example.application.service.security.DecryptSymmetricService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenDecoderFactory;
import org.springframework.security.oauth2.client.oidc.authentication.OidcIdTokenValidator;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistration.ClientSettings;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoderFactory;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class WebClientConfiguration {

    @Autowired
    DecryptSymmetricService decryptSymmetricService;

    @Bean("dbApiClientRegistrationRepo")
    ClientRegistrationRepository getRegistration(
            @Value("${spring.cloud.gcp.kms.project-id}") String projectId,
            @Value("${spring.cloud.gcp.kms.location-id}") String locationId,
            @Value("${spring.cloud.gcp.kms.key-ring-id}") String keyRingId,
            @Value("${spring.cloud.gcp.kms.key-id}") String keyId,
            @Value("${spring.cloud.gcp.storage.bucket-name}") String bucketName,
            @Value("${spring.security.oauth2.client.registration.dbApi.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.dbApi.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.registration.dbApi.redirect-uri}") String dbApiRedirectUri,
            @Value("${spring.security.oauth2.client.registration.dbApi.scope}") String scope,
            @Value("${spring.security.oauth2.client.provider.dbApi.authorization-uri}") String authorizationUri,
            @Value("${spring.security.oauth2.client.provider.dbApi.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.provider.dbApi.jwk-set-uri}") String jwkSetUri,
            @Value("${spring.security.oauth2.client.provider.dbApi.user-info-uri}") String userInfoUri,
            @Value("${spring.security.oauth2.client.registration.github.client-id}") String gitHubClientId,
            @Value("${spring.security.oauth2.client.registration.github.redirect-uri}") String gitHubRedirectUri
    ) {

        List<String> scopes = Stream.of(scope.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        byte[] cipherText = BucketService.getCredentialsFromGcpBucket(projectId, bucketName);
        String decryptedText = decryptSymmetricService.decryptSymmetric(projectId, locationId, keyRingId, keyId, cipherText);

        Gson gson = new Gson();
        ClientConfig clientConfig = gson.fromJson(decryptedText, ClientConfig.class);

        ClientRegistration dbApiClientRegistration = ClientRegistration
                .withRegistrationId("dbApi")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .redirectUri(dbApiRedirectUri)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .jwkSetUri(jwkSetUri)
                .userInfoUri(userInfoUri)
                .scope(scopes)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                // IMPORTANT: ClientSettings.Builder#requireProofKey defaults to true in
                // Spring Security. DefaultOAuth2AuthorizationRequestResolver adds PKCE
                // (code_challenge/code_challenge_method) whenever requireProofKey is
                // true OR the client authentication method is NONE - independent of
                // scopes. This "HolgerSampleGaeAppWithoutPkce" client must never use
                // PKCE, so requireProofKey is explicitly disabled here, instead of
                // relying solely on stripping the params afterwards in a custom
                // OAuth2AuthorizationRequestResolver.
                .clientSettings(ClientSettings.builder().requireProofKey(false).build())
                .build();

        ClientRegistration githubClientRegistration =
                CommonOAuth2Provider.GITHUB.getBuilder("github")
                        .clientId(gitHubClientId)
                        .clientSecret(clientConfig.getGitHubClientSecret())
                        .redirectUri(gitHubRedirectUri)
                        .build();

        return new InMemoryClientRegistrationRepository(dbApiClientRegistration, githubClientRegistration);
    }

    /**
     * IMPORTANT: Deutsche Bank's OIDC discovery document (.well-known/openid-configuration)
     * advertises "id_token_signing_alg_values_supported": ["RS256"], but the JWK Set
     * actually published at the jwks_uri only contains an EC P-256 key (kty: "EC",
     * crv: "P-256"), which corresponds to the ES256 algorithm - not RS256. Spring
     * Security's default OidcIdTokenDecoderFactory/jwsAlgorithmResolver always expects
     * RS256 for every ClientRegistration, regardless of what the provider's metadata/
     * JWKS actually contain, and only accepts a single fixed algorithm. Since we can't
     * be 100% sure from the provider's (inconsistent) metadata alone whether the
     * actually-issued ID tokens are signed with RS256 or ES256, the decoder below
     * accepts EITHER algorithm for the "dbApi" registration - Nimbus will pick whichever
     * one matches the token's "alg" header and the key(s) actually published at the
     * jwks_uri. Every other registration (e.g. github) keeps using Spring Security's
     * standard RS256-only OidcIdTokenDecoderFactory.
     */
    @Bean
    JwtDecoderFactory<ClientRegistration> idTokenDecoderFactory() {
        OidcIdTokenDecoderFactory defaultDecoderFactory = new OidcIdTokenDecoderFactory();
        return clientRegistration -> {
            if (!"dbApi".equals(clientRegistration.getRegistrationId())) {
                return defaultDecoderFactory.createDecoder(clientRegistration);
            }
            NimbusJwtDecoder nimbusJwtDecoder = NimbusJwtDecoder
                    .withJwkSetUri(clientRegistration.getProviderDetails().getJwkSetUri())
                    .jwsAlgorithm(SignatureAlgorithm.RS256)
                    .jwsAlgorithm(SignatureAlgorithm.ES256)
                    .build();
            OAuth2TokenValidator<Jwt> tokenValidator = new DelegatingOAuth2TokenValidator<>(
                    new JwtTimestampValidator(), new OidcIdTokenValidator(clientRegistration));
            nimbusJwtDecoder.setJwtValidator(tokenValidator);
            return (JwtDecoder) nimbusJwtDecoder;
        };
    }

}




