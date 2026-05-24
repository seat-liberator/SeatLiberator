package com.seatliberator.seatliberator.reservation.web.shared.security;

import com.seatliberator.seatliberator.identity.core.role.Role;
import com.seatliberator.seatliberator.identity.core.role.RoleCapabilities;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

import static com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability.*;

@Configuration
public class ReservationRoleCapabilityConfiguration {
    @Bean
    RoleCapabilities guestRoleCapabilities() {
        return new RoleCapabilities(Role.GUEST, Set.of(
                ROOM_LIST,
                SEAT_LIST
        ));
    }

    @Bean
    RoleCapabilities userRoleCapabilities() {
        return new RoleCapabilities(Role.USER, Set.of(
                ROOM_READ,
                SEAT_READ,
                BOOKING_CREATE,
                OWNED_BOOKING_UPDATE,
                OWNED_BOOKING_CANCEL
        ));
    }

    @Bean
    RoleCapabilities maintainerRoleCapabilities() {
        return new RoleCapabilities(Role.MAINTAINER, Set.of(
                ROOM_MANAGE,
                SEAT_MANAGE,
                BOOKING_MANAGE
        ));
    }

    @Bean
    RoleCapabilities adminRoleCapabilities() {
        return new RoleCapabilities(Role.ADMIN, Set.of());
    }
}
