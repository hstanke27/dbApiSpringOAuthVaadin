package com.example.application.security;

import com.example.application.security.oauth.NoPkceAuthorizationRequestResolver;
import com.vaadin.flow.spring.security.UidlRedirectStrategy;
import com.vaadin.flow.spring.security.VaadinSecurityConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

@Configuration
public class SecurityConfiguration {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   OAuth2AuthorizedClientRepository authorizedClientRepository,
                                                   OAuth2AuthorizedClientService authorizedClientService) throws Exception {

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/login/oauth2/code/**", "/oauth2/authorization/**").permitAll()
        );

        // dbAPI's simulator either does not expose a reliably reachable OIDC UserInfo
        // endpoint, or does not return a usable "user name" attribute, so the default
        // OidcUserService fails with "missing_user_name_attribute"/"missing_user_info_uri"
        // before even attempting the call. Since all the data we need (given_name,
        // family_name, email, sub, etc.) is already present as claims in the ID token
        // itself, we skip the UserInfo Endpoint call entirely via
        // OidcUserService#setRetrieveUserInfo(Predicate) and let Spring Security build
        // the OidcUser purely from the validated ID token claims.
        OidcUserService oidcUserService = new OidcUserService();
        oidcUserService.setRetrieveUserInfo(userRequest -> false);

        http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestResolver(new NoPkceAuthorizationRequestResolver(clientRegistrationRepository))
                )
                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService))
                .successHandler((request, response, authentication) -> {
                    // 1. Sicherstellen, dass es ein OAuth2-Login ist
                    if (authentication instanceof OAuth2AuthenticationToken oauthToken) {

                    }

                    // Harter, direkter Redirect auf /start
                    response.sendRedirect("/start");
                })
        ).logout(logout -> logout
                .permitAll()
                .deleteCookies("JSESSIONID")
        );

        // IMPORTANT: Logout triggered via Vaadin's AuthenticationContext#logout()
        // (used by the Logout buttons in StartLayout/MainLayout) runs during an
        // internal Vaadin UIDL/XHR request, which expects a JSON response back -
        // not a plain HTTP redirect. The plain SimpleUrlLogoutSuccessHandler used
        // by logoutSuccessUrl("/") above issues a normal HTTP redirect, which the
        // client-side fetch call can't parse as JSON, causing
        // "Invalid JSON response from server" with the raw login-page HTML as
        // the (mis-parsed) body. Vaadin's UidlRedirectStrategy detects this case
        // and performs a client-side ui.getPage().setLocation(url) navigation
        // instead, so we explicitly wire it into the logout success handler here.
        SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        logoutSuccessHandler.setRedirectStrategy(new UidlRedirectStrategy());
        logoutSuccessHandler.setDefaultTargetUrl("/");

        http.with(VaadinSecurityConfigurer.vaadin(), configurer -> configurer
                .logoutSuccessHandler(logoutSuccessHandler));

        return http.build();
    }
}