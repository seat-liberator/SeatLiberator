package com.seatliberator.seatliberator.bootstrap.security;

import com.seatliberator.seatliberator.identity.client.actor.ActorContextHolder;
import com.seatliberator.seatliberator.identity.core.actor.Actor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@NullMarked
public class ActorContextBindingFilter extends OncePerRequestFilter {
    private final ActorContextHolder actorContextHolder;

    public ActorContextBindingFilter(ActorContextHolder actorContextHolder) {
        this.actorContextHolder = actorContextHolder;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        var auth = SecurityContextHolder
                .getContext().getAuthentication();

        var principal = Optional.ofNullable(auth)
                .map(Authentication::getPrincipal)
                .filter(e -> e instanceof Actor)
                .map(Actor.class::cast);

        principal.ifPresent(actorContextHolder::setActor);

        try {
            filterChain.doFilter(request, response);
        } finally {
            actorContextHolder.clear();
        }
    }
}

