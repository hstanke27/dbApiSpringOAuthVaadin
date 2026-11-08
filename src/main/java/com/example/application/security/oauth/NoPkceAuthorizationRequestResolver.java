package com.example.application.security.oauth;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import jakarta.servlet.http.HttpServletRequest;

public class NoPkceAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public NoPkceAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        // Der Standard-Resolver weiß genau, wie Registrierungen und States verwaltet werden
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        return removePkce(authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        return removePkce(authorizationRequest);
    }

    private OAuth2AuthorizationRequest removePkce(OAuth2AuthorizationRequest authorizationRequest) {
        if (authorizationRequest == null) {
            return null;
        }

        // IMPORTANT: OAuth2AuthorizationRequest.from(...) already copies the ORIGINAL
        // additionalParameters (including code_challenge/code_challenge_method) into
        // the new builder's internal map via Map#putAll. Calling
        // .additionalParameters(Map) again afterwards only merges keys in - it can
        // never remove keys that are already present (Map#putAll never deletes).
        // So passing a "reduced" map here silently fails to strip the PKCE params.
        // Using the Consumer overload instead gives direct access to the actual
        // internal map, so remove() really deletes the keys.
        return OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(params -> {
                    params.remove("code_challenge");
                    params.remove("code_challenge_method");
                })
                .attributes(attrs -> attrs.remove("code_verifier"))
                .build();
    }
}