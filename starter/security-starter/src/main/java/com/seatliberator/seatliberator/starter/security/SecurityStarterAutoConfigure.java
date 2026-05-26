package com.seatliberator.seatliberator.starter.security;

import com.seatliberator.seatliberator.starter.security.customizer.HttpSecurityCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AutoConfiguration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnClass({HttpSecurity.class, SecurityFilterChain.class})
@ConditionalOnProperty(
        prefix = "seatliberator.starter.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties(SecurityStarterProperties.class)
public class SecurityStarterAutoConfigure {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            ObjectProvider<CorsConfigurationSource> corsConfigurationSourceObjectProvider,
            List<HttpSecurityCustomizer> httpSecurityCustomizers,
            SecurityStarterProperties properties
    ) throws Exception {
        configureCsrf(httpSecurity, properties);
        configureCors(httpSecurity, corsConfigurationSourceObjectProvider, properties);

        httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        var httpCustomizers = new ArrayList<>(httpSecurityCustomizers);
        AnnotationAwareOrderComparator.sort(httpCustomizers);

        for (var customizer : httpCustomizers) customizer.customize(httpSecurity);

        return httpSecurity.build();
    }

    @Bean
    @ConditionalOnMissingBean(CorsConfigurationSource.class)
    @ConditionalOnProperty(
            prefix = "seatliberator.starter.security.cors",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    UrlBasedCorsConfigurationSource corsConfigurationSource(SecurityStarterProperties properties) {
        var cors = properties.cors();

        var configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(cors.allowedOrigins());
        configuration.setAllowedMethods(cors.allowedMethods());
        configuration.setAllowedHeaders(cors.allowedHeaders());
        configuration.setAllowCredentials(cors.allowCredentials());
        configuration.setExposedHeaders(cors.exposedHeaders());
        configuration.validateAllowCredentials();

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean("permitAllRequestsCustomizer")
    @ConditionalOnMissingBean(name = "permitAllRequestsCustomizer")
    @ConditionalOnProperty(
            prefix = "seatliberator.starter.security.authorize",
            name = "enabled",
            havingValue = "true",
            matchIfMissing = true
    )
    @Order(Ordered.LOWEST_PRECEDENCE)
    HttpSecurityCustomizer permitAllRequestsCustomizer(SecurityStarterProperties properties) {
        var authorize = properties.authorize();

        return httpSecurity -> httpSecurity.authorizeHttpRequests(registry -> {
            for (var permit : authorize.permits())
                registry.requestMatchers(permit).permitAll();

            switch (authorize.anyRequest()) {
                case AUTHENTICATED -> registry.anyRequest().authenticated();
                case DENY_ALL -> registry.anyRequest().denyAll();
            }
        });
    }

    private void configureCsrf(HttpSecurity httpSecurity, SecurityStarterProperties properties) {
        if (properties.csrfEnabled()) httpSecurity.csrf(Customizer.withDefaults());
        else httpSecurity.csrf(AbstractHttpConfigurer::disable);
    }

    private void configureCors(
            HttpSecurity httpSecurity,
            ObjectProvider<CorsConfigurationSource> corsConfigurationSourceObjectProvider,
            SecurityStarterProperties properties
    ) {
        var cors = properties.cors();

        if (cors.enabled()) {
            var corsSource = Optional.ofNullable(corsConfigurationSourceObjectProvider.getIfAvailable())
                    .orElseThrow(() -> new IllegalStateException("""
                            CORS is enabled but CorsConfigurationSource bean is not available.
                            Check seatliberator.starter.security.cors.enabled or define a CorsConfigurationSource bean.
                            """));

            httpSecurity.cors(configurer -> configurer.configurationSource(corsSource));
        } else {
            httpSecurity.cors(AbstractHttpConfigurer::disable);
        }
    }
}
