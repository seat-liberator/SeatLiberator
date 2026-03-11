package com.seatliberator.seatliberator.identity.infrastructure.security.authentication.method.federated.mapper;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DefaultFederatedPrincipalMapperRegistry implements FederatedPrincipalMapperRegistry {
    private final Map<String, FederatedPrincipalMapper> registry;

    public DefaultFederatedPrincipalMapperRegistry(
            List<FederatedPrincipalMapper> federatedPrincipalMappers
    ) {
        this.registry = federatedPrincipalMappers.stream()
                .collect(Collectors.toMap(
                        FederatedPrincipalMapper::key,
                        Function.identity()
                ));

        var availableRegistrationIds = federatedPrincipalMappers.stream()
                .map(FederatedPrincipalMapper::key)
                .toList();

        log.debug("Registered {} mappers. available registration id={}", federatedPrincipalMappers.size(), availableRegistrationIds);
    }

    @Override
    public void registerProfileMapper(FederatedPrincipalMapper federatedPrincipalMapper) {
        registry.putIfAbsent(federatedPrincipalMapper.key(), federatedPrincipalMapper);
    }

    @Override
    public @Nullable FederatedPrincipalMapper resolve(String registrationId) {
        return registry.get(registrationId);
    }
}
