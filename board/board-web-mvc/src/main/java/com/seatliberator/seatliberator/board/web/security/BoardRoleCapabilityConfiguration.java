package com.seatliberator.seatliberator.board.web.security;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.RoleCapabilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.seatliberator.seatliberator.board.application.config.BoardCapability.*;

@Configuration
public class BoardRoleCapabilityConfiguration {
    @Bean
    RoleCapabilities guestRoleCapabilities() {
        return new RoleCapabilities(Role.GUEST, Set.of(
                POST_LIST,
                CATEGORY_LIST
        ));
    }

    @Bean
    RoleCapabilities userRoleCapabilities() {
        return new RoleCapabilities(Role.USER, Set.of(
                POST_READ,
                POST_CREATE,
                OWNED_POST_UPDATE,
                OWNED_POST_DELETE,
                COMMENT_CREATE,
                OWNED_COMMENT_UPDATE,
                OWNED_COMMENT_DELETE
        ));
    }

    @Bean
    RoleCapabilities maintainerRoleCapabilities() {
        return new RoleCapabilities(Role.MAINTAINER, Set.of(
                POST_MANAGE,
                COMMENT_MANAGE,
                CATEGORY_CREATE,
                CATEGORY_MANAGE
        ));
    }

    @Bean
    RoleCapabilities adminRoleCapabilities() {
        return new RoleCapabilities(Role.ADMIN, Set.of());
    }
}