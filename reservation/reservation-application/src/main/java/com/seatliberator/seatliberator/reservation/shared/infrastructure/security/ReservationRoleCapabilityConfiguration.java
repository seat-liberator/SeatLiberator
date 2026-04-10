package com.seatliberator.seatliberator.reservation.shared.infrastructure.security;

import com.seatliberator.seatliberator.identity.client.role.RoleCapabilities;
import com.seatliberator.seatliberator.identity.core.role.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.seatliberator.seatliberator.reservation.shared.infrastructure.security.ReservationCapability.*;

@Configuration
public class ReservationRoleCapabilityConfiguration {
    @Bean
    RoleCapabilities guestRoleCapabilities() {
        return new RoleCapabilities(Role.GUEST, Set.of(
                SEAT_LIST
        ));
    }

    @Bean
    RoleCapabilities userRoleCapabilities() {
        return new RoleCapabilities(Role.USER, Set.of(
                SEAT_READ,
                BOOKING_CREATE,
                OWNED_BOOKING_UPDATE,
                OWNED_BOOKING_CANCEL
        ));
    }

    @Bean
    RoleCapabilities maintainerRoleCapabilities() {
        return new RoleCapabilities(Role.MAINTAINER, Set.of(
                SEAT_MANAGE,
                BOOKING_MANAGE
        ));
    }

    @Bean
    RoleCapabilities adminRoleCapabilities() {
        return new RoleCapabilities(Role.ADMIN, Set.of());
    }
}
