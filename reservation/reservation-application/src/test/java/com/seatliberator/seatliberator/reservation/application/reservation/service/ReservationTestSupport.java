package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.DefaultTestSupport;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;

import java.util.UUID;

public class ReservationTestSupport extends DefaultTestSupport {
    public static final UUID RESERVATION_ID = UuidGenerator.generate(1);
    public static final String OTHER_USER_ID = "user-2";
    public static final Actor OTHER_ACTOR = new ActorFixture.Builder().subject(OTHER_USER_ID).build();

    public static Reservation reservation() {
        return ReservationFixture.createReservation();
    }

    public static Reservation reservationWithId() {
        var reservation = reservation();
        ReservationFixture.stubReservationId(reservation, RESERVATION_ID);
        return reservation;
    }

    public static UseReservationCommand useReservationCommand() {
        return new UseReservationCommand(RESERVATION_ID, ACTOR);
    }

    public static UseReservationCommand useReservationCommand(Actor actor) {
        return new UseReservationCommand(RESERVATION_ID, actor);
    }
}
