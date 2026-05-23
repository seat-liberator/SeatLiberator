package com.seatliberator.seatliberator.identity.client.autoconfigure;

import com.seatliberator.seatliberator.identity.client.web.ActorContextBindingFilter;
import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = IdentityClientAutoConfiguration.class)
@ConditionalOnClass(name = {
        "jakarta.servlet.Filter",
        "org.springframework.security.core.context.SecurityContextHolder",
        "org.springframework.web.filter.OncePerRequestFilter"
})
public class IdentityClientServletAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    ActorContextBindingFilter actorContextBindingFilter(ActorContextHolder actorContextHolder) {
        return new ActorContextBindingFilter(actorContextHolder);
    }
}
