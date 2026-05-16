package com.seatliberator.seatliberator.reservation.application.reservation.contract;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@RequiredArgsConstructor
public class ReservationAuthorizedCreator {
    private final CreateAuthorizer authorizer;
    private final Clock clock;


}
