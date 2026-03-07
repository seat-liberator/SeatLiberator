package com.seatliberator.seatliberator.identity.core;

import com.seatliberator.seatliberator.identity.core.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.ActorFactory;
import com.seatliberator.seatliberator.identity.core.actor.SimpleActorFactory;
import com.seatliberator.seatliberator.identity.core.actor.ThreadLocalActorContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
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
}
