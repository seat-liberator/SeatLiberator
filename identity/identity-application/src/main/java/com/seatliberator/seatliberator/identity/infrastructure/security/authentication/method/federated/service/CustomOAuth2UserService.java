package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.service;

import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.principal.CustomOAuth2Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.debug("Attempting OAuth2 user loading.");

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.debug("OAuth2 user loaded from provider. registrationId={}", registrationId);

        var federatedPrincipalMapper = Optional.ofNullable(federatedPrincipalMapperRegistry.resolve(registrationId))
                .orElseThrow(() -> {
                    log.debug(
                            "OAuth2 user loading failed because no federated principal mapper was found. registrationId={}",
                            registrationId
                    );
                    return new IllegalStateException(
                            "No profile mapper found for registrationId=" + registrationId
                    );
                });

        log.debug("Federated principal mapper resolved. registrationId={}", registrationId);


        var principal = (CustomOAuth2Principal) federatedPrincipalMapper.resolve(oAuth2User);

        log.debug("OIDC principal mapping succeeded. registrationId={}", registrationId);


        return principal;
    }
}
