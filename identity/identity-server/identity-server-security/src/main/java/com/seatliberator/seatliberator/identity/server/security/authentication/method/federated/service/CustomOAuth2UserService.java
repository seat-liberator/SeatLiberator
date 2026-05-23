package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.service;

import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.CustomOAuth2Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final FederatedPrincipalMapperRegistry registry;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = super.loadUser(userRequest);

        var registrationId = userRequest.getClientRegistration().getRegistrationId();

        var mapper = registry.getByKey(registrationId);
        if (mapper == null) {
            throw authenticationException("No principal mapper found. registrationId=" + registrationId);
        }

        try {
            return (CustomOAuth2Principal) mapper.resolve(oAuth2User);
        } catch (IllegalArgumentException e) {
            throw authenticationException(e.getMessage());
        }
    }

    private OAuth2AuthenticationException authenticationException(String message) {
        return new OAuth2AuthenticationException(
                new OAuth2Error("invalid_federated_principal"),
                message
        );
    }
}
