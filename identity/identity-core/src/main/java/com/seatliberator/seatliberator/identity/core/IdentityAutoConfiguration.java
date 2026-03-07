package com.seatliberator.seatliberator.identity.core;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.ActorFactory;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActorFactory;
import com.seatliberator.seatliberator.identity.core.actor.ThreadLocalActorContextHolder;
import com.seatliberator.seatliberator.identity.core.introspect.*;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(IntrospectionConfigurationProperties.class)
public class IdentityAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(ActorContextHolder.class)
    ActorContextHolder actorContextHolder() {
        return new ThreadLocalActorContextHolder();
    }

    @Bean
    @ConditionalOnMissingBean(ActorFactory.class)
    ActorFactory actorFactory() {
        return new SimpleActorFactory();
    }

    @Bean
    @ConditionalOnMissingBean(IntrospectionFactory.class)
    IntrospectionFactory introspectionFactory() {
        return new SimpleIntrospectionFactory();
    }

    @Bean
    @ConditionalOnMissingBean(Introspector.class)
    Introspector introspector(
            ActorFactory actorFactory,
            IntrospectionFactory introspectionFactory,
            IntrospectionConfigurationProperties properties
    ) {
        Long expiration = properties.expirationMs();
        return new BypassIntrospector(
                actorFactory,
                introspectionFactory,
                expiration
        );
    }
}
