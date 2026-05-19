package com.seatliberator.seatliberator.reservation.persistence.booking.jpa;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.BookingDetailReader;
import com.seatliberator.seatliberator.reservation.persistence.booking.jpa.repository.BookingDetailRepository;
import com.seatliberator.seatliberator.reservation.persistence.booking.jpa.row.BookingDetailRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBookingDetailPersistenceAdapter implements BookingDetailReader {
    private final BookingDetailRepository repository;

    @Override
    public Optional<BookingDetailResult> findByReservationId(UUID reservationId) {
        var rows = repository.findRowsByReservationId(reservationId);
        if (rows.isEmpty()) return Optional.empty();

        var first = rows.getFirst();
        var state = first.toReservationStateResult();
        var slots = rows.stream()
                .filter(row -> row.seatTimeSlotId() != null)
                .map(BookingDetailRow::toSlotResult)
                .toList();

        return Optional.of(new BookingDetailResult(
                first.reservationId(),
                first.userId(),
                state,
                slots
        ));
    }
}
