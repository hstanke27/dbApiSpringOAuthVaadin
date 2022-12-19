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
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

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
                //.clientSecret(clientConfig.getDbApiClientSecret())
                .redirectUri(dbApiRedirectUri)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .jwkSetUri(jwkSetUri)
                .userInfoUri(userInfoUri)
                .scope(scopes)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        ClientRegistration githubClientRegistration =
                CommonOAuth2Provider.GITHUB.getBuilder("github")
                        .clientId(gitHubClientId)
                        .clientSecret(clientConfig.getGitHubClientSecret())
                        .redirectUri(gitHubRedirectUri)
                        .build();

        return new InMemoryClientRegistrationRepository(dbApiClientRegistration, githubClientRegistration);
    }

}




