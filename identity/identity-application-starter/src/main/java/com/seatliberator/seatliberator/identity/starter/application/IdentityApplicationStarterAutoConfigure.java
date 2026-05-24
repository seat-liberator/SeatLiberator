package com.seatliberator.seatliberator.identity.starter.application;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.context.ThreadLocalActorContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(
        prefix = "identity.starter.application",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class IdentityApplicationStarterAutoConfigure {
    @Bean
    @ConditionalOnMissingBean(ActorContextHolder.class)
    ThreadLocalActorContextHolder actorContextHolder() {
        return new ThreadLocalActorContextHolder();
    }
}
