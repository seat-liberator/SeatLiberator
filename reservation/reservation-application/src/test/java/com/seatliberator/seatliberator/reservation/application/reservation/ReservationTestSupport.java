package com.seatliberator.seatliberator.reservation.application.reservation;

import com.seatliberator.seatliberator.identity.core.actor.Actor;
import com.seatliberator.seatliberator.identity.core.actor.ActorFixture;
import com.seatliberator.seatliberator.kernel.test.UuidGenerator;
import com.seatliberator.seatliberator.reservation.application.DefaultTestSupport;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.command.UseReservationCommand;
import com.seatliberator.seatliberator.reservation.application.shared.configuration.ReservationCapability;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.ReservationFixture;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Fail.fail;

public class ReservationTestSupport extends DefaultTestSupport {
    public static final UUID RESERVATION_ID = UuidGenerator.generate(1);
    public static final String OTHER_USER_ID = "user-2";
    public static final Actor OTHER_ACTOR = new ActorFixture.Builder().subject(OTHER_USER_ID).build();
    public static final Actor BOOKING_CANCELER = new ActorFixture.Builder()
            .subject(USER_ID)
            .capabilities(Set.of(ReservationCapability.OWNED_BOOKING_CANCEL))
            .build();
    public static final Actor BOOKING_CREATOR = new ActorFixture.Builder()
            .subject(USER_ID)
            .capabilities(Set.of(ReservationCapability.BOOKING_CREATE))
            .build();
    public static final Actor BOOKING_READER = new ActorFixture.Builder()
            .subject(USER_ID)
            .capabilities(Set.of(ReservationCapability.BOOKING_READ))
            .build();
    public static final Actor BOOKING_MANAGER = new ActorFixture.Builder()
            .subject(OTHER_USER_ID)
            .capabilities(Set.of(ReservationCapability.BOOKING_MANAGE))
            .build();

    public static Reservation reservation() {
        return ReservationFixture.createReservation();
    }

    public static Reservation reservationWithId() {
        var reservation = reservation();
        stubReservationId(reservation, RESERVATION_ID);
        return reservation;
    }

    public static void stubReservationId(Reservation reservation, UUID id) {
        try {
            var idField = Reservation.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(reservation, id);
        } catch (ReflectiveOperationException e) {
            fail("테스트용 ID 설정 실패");
        }
    }

    public static UseReservationCommand useReservationCommand() {
        return new UseReservationCommand(RESERVATION_ID);
    }
}
