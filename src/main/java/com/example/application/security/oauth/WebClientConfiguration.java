package com.example.application.security.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    @Bean("dbApiClientRegistrationRepo")
    ClientRegistrationRepository getRegistration(
            @Value("${spring.security.oauth2.client.registration.dbApi.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.dbApi.client-secret}") String clientSecret,
            @Value("${spring.security.oauth2.client.registration.dbApi.redirect-uri}") String redirectUri,
            @Value("${spring.security.oauth2.client.registration.dbApi.scope}") String scope,
            @Value("${spring.security.oauth2.client.provider.dbApi.authorization-uri}") String authorizationUri,
            @Value("${spring.security.oauth2.client.provider.dbApi.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.provider.dbApi.jwk-set-uri}") String jwkSetUri,
            @Value("${spring.security.oauth2.client.provider.dbApi.user-info-uri}") String userInfoUri,
            @Value("${spring.security.oauth2.client.registration.github.client-id}") String gitHubClientId,
            @Value("${spring.security.oauth2.client.registration.github.client-secret}") String gitHubClientSecret

    ) {

        List<String> scopes = Stream.of(scope.split(","))
                .map(String::trim)
                .collect(Collectors.toList());

        ClientRegistration dbApiRegistration = ClientRegistration
                .withRegistrationId("dbApi")
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .jwkSetUri(jwkSetUri)
                .userInfoUri(userInfoUri)
                .scope(scopes)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();

        ClientRegistration githubRegristration =
                CommonOAuth2Provider.GITHUB.getBuilder("github")
                        .clientId(gitHubClientId)
                        .clientSecret(gitHubClientSecret)
                        .build();

        return new InMemoryClientRegistrationRepository(dbApiRegistration, githubRegristration);
    }

}
