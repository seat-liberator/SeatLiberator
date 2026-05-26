package com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.config;

import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapper;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.FederatedPrincipalMapperRegistry;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.GithubOAuth2PrincipalMapper;
import com.seatliberator.seatliberator.identity.server.security.authentication.method.federated.mapper.GoogleOidcPrincipalMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FederatedPrincipalMapperRegistryConfiguration {
    @Bean
    GoogleOidcPrincipalMapper googleOidcPrincipalMapper() {
        return new GoogleOidcPrincipalMapper();
    }

    @Bean
    GithubOAuth2PrincipalMapper githubOAuth2PrincipalMapper() {
        return new GithubOAuth2PrincipalMapper();
    }

    @Bean
    FederatedPrincipalMapperRegistry federatedPrincipalMapperRegistry(List<FederatedPrincipalMapper> mappers) {
        return new FederatedPrincipalMapperRegistry(mappers);
    }
}
