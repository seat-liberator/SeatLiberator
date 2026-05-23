package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class FederatedPrincipalMapperRegistry {
    private final Map<String, FederatedPrincipalMapper> registry;

    public FederatedPrincipalMapperRegistry(List<FederatedPrincipalMapper> mappers) {
        Preconditions.requireNonNull(mappers, "mappers");

        this.registry = mappers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        FederatedPrincipalMapper::key,
                        Function.identity()
                ));
    }

    public FederatedPrincipalMapper getByKey(String key) {
        return registry.get(key);
    }
}
