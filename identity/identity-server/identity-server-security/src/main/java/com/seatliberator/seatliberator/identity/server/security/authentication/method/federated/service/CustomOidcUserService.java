package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.service;

import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.principal.CustomOidcPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

@Slf4j
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final FederatedPrincipalMapperRegistry registry;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var oidcUser = super.loadUser(userRequest);

        var registrationId = userRequest.getClientRegistration().getRegistrationId();

        var mapper = registry.getByKey(registrationId);
        if (mapper == null) {
            throw authenticationException("No principal mapper found. registrationId=" + registrationId);
        }

        try {
            return (CustomOidcPrincipal) mapper.resolve(oidcUser);
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
