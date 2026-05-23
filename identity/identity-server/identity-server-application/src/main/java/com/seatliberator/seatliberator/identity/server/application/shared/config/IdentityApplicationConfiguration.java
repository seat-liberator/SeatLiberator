package com.seatliberator.seatliberator.identity.server.application.shared.config;

import com.seatliberator.seatliberator.identity.api.IdentityApi;
import com.seatliberator.seatliberator.kernel.FixedCurrentApplicationNamespaceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class IdentityApplicationConfiguration {
    @Bean
    FixedCurrentApplicationNamespaceProvider fixedCurrentApplicationNamespaceProvider() {
        return new FixedCurrentApplicationNamespaceProvider(IdentityApi.NAMESPACE);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
