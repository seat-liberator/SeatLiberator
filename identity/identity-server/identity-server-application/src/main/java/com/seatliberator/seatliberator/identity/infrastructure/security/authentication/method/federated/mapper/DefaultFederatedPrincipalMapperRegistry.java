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


        log.debug(
                "Registered federated principal mappers. count={}, registrationIds={}",
                federatedPrincipalMappers.size(),
                availableRegistrationIds
        );
    }

    @Override
    public void registerProfileMapper(FederatedPrincipalMapper federatedPrincipalMapper) {
        var registrationId = federatedPrincipalMapper.key();

        if (registry.containsKey(registrationId)) {
            log.debug(
                    "Skipped federated principal mapper registration because mapper already exists. registrationId={}",
                    registrationId
            );
            return;
        }

        registry.put(registrationId, federatedPrincipalMapper);

        log.debug(
                "Registered federated principal mapper. registrationId={}, mapperType={}",
                registrationId,
                federatedPrincipalMapper.getClass().getSimpleName()
        );
    }

    @Override
    public @Nullable FederatedPrincipalMapper resolve(String registrationId) {
        var mapper = registry.get(registrationId);

        if (mapper == null) {
            log.debug(
                    "Federated principal mapper not found. registrationId={}",
                    registrationId
            );
            return null;
        }

        log.debug(
                "Federated principal mapper resolved. registrationId={}, mapperType={}",
                registrationId,
                mapper.getClass().getSimpleName()
        );

        return mapper;
    }
}
