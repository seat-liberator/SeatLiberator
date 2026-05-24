package com.seatliberator.seatliberator.bootstrap.autoconfigure;

import com.seatliberator.seatliberator.bootstrap.security.customizer.ResourceServerHttpSecurityCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
@ConditionalOnClass({HttpSecurity.class, SecurityFilterChain.class, JwtDecoder.class})
@ConditionalOnProperty(
        prefix = "seatliberator.resource-server.security",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@EnableConfigurationProperties({ResourceServerSecurityProperties.class, ResourceServerAuthorizeProperties.class})
public class ResourceServerSecurityAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    SecurityFilterChain resourceServerSecurityFilterChain(
            HttpSecurity http,
            ResourceServerSecurityProperties securityProperties,
            ObjectProvider<List<ResourceServerHttpSecurityCustomizer>> httpCustomizerProvider
    ) throws Exception {
        if (!securityProperties.csrfEnabled()) http.csrf(CsrfConfigurer::disable);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        var httpCustomizers = new ArrayList<>(httpCustomizerProvider.getIfAvailable(ArrayList::new));
        AnnotationAwareOrderComparator.sort(httpCustomizers);
        for (var customizer : httpCustomizers) customizer.customize(http);

        return http.build();
    }
}
