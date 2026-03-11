package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.service;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOidcPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {
    private final FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("Attempting OIDC user loading.");

        OidcUser oidcUser = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("OIDC user loaded from provider. registrationId={}", registrationId);

        var federatedPrincipalMapper = Optional.ofNullable(federatedPrincipalMapperRegistry.resolve(registrationId))
                .orElseThrow(() -> {
                    log.debug(
                            "OIDC user loading failed because no federated principal mapper was found. registrationId={}",
                            registrationId
                    );
                    return new IllegalStateException(
                            "No profile mapper found for registrationId=" + registrationId
                    );
                });

        log.debug("Federated principal mapper resolved. registrationId={}", registrationId);

        var principal = (CustomOidcPrincipal) federatedPrincipalMapper.resolve(oidcUser);

        log.debug("OIDC principal mapping succeeded. registrationId={}", registrationId);

        return principal;
    }
}
