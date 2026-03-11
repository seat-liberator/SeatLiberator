package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.service;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper.FederatedPrincipalMapper;
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
        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("Get registrationId={}, from request={}", registrationId, userRequest);

        FederatedPrincipalMapper federatedPrincipalMapper = Optional.ofNullable(federatedPrincipalMapperRegistry.resolve(registrationId))
                .orElseThrow(() -> new IllegalStateException("No profile mapper founded that support this registration id=" + registrationId));

        return (CustomOidcPrincipal) federatedPrincipalMapper.resolve(oidcUser);
    }
}
