package com.seatliberator.seatliberator.reservation.persistence.booking.jpa;

import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.out.BookingDetailReader;
import com.seatliberator.seatliberator.reservation.domain.reservation.Reservation;
import com.seatliberator.seatliberator.reservation.domain.reservation.SeatOccupancy;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.domain.seat.SeatTimeSlot;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.occupancy.jpa.repository.SeatOccupancyRepository;
import com.seatliberator.seatliberator.reservation.persistence.reservation.jpa.repository.ReservationRepository;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatTimeSlotRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;

import static com.seatliberator.seatliberator.reservation.persistence.booking.BookingTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaBookingDetailPersistenceAdapter.class})
@DisplayName("BookingDetail Persistence")
public class JpaBookingDetailPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    BookingDetailReader reader;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    SeatOccupancyRepository seatOccupancyRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatTimeSlotRepository seatTimeSlotRepository;

    private Room saveRoom() {
        return roomRepository.save(room());
    }

    private Seat saveSeat(Room room) {
        return seatRepository.save(seat(room));
    }

    private SeatTimeSlot saveSeatTimeSlot(Seat seat, LocalTime startAt) {
        return seatTimeSlotRepository.save(seatTimeSlot(seat, startAt));
    }

    private Reservation saveReservation() {
        return saveReservation(USER_ID);
    }

    private Reservation saveReservation(String userId) {
        return reservationRepository.save(reservation(userId));
    }

    private SeatOccupancy saveSeatOccupancy(SeatTimeSlot slot, Reservation reservation, LocalDate occupancyDate) {
        return seatOccupancyRepository.save(seatOccupancy(slot, reservation, occupancyDate));
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("findByReservationId는 예약과 점유 슬롯 상세를 함께 반환한다")
        void should_find_booking_detail_by_reservation_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var firstSlot = saveSeatTimeSlot(seat, LocalTime.of(9, 0));
            var secondSlot = saveSeatTimeSlot(seat, LocalTime.of(11, 0));
            var reservation = saveReservation();
            var firstOccupancy = saveSeatOccupancy(firstSlot, reservation, OCCUPANCY_DATE);
            var secondOccupancy = saveSeatOccupancy(secondSlot, reservation, OCCUPANCY_DATE);
            saveSeatOccupancy(firstSlot, saveReservation(OTHER_USER_ID), OCCUPANCY_DATE.plusDays(1));
            flushAndClear();

            var actual = reader.findByReservationId(reservation.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(detail -> {
                        assertReservation(detail, reservation);
                        assertThat(detail.slots())
                                .extracting(BookingDetailResult.BookingSlotResult::seatTimeSlotId)
                                .containsExactly(firstSlot.getId(), secondSlot.getId());
                        assertSlot(detail.slots().get(0), firstOccupancy, firstSlot, room, seat, LocalTime.of(9, 0));
                        assertSlot(detail.slots().get(1), secondOccupancy, secondSlot, room, seat, LocalTime.of(11, 0));
                    });
        }

        @Test
        @DisplayName("findByReservationId는 예약이 있지만 점유 슬롯이 없으면 빈 슬롯 목록을 반환한다")
        void should_return_empty_slots_when_reservation_has_no_occupancy() {
            var reservation = saveReservation();
            flushAndClear();

            var actual = reader.findByReservationId(reservation.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(detail -> {
                        assertReservation(detail, reservation);
                        assertThat(detail.slots()).isEmpty();
                    });
        }

        @Test
        @DisplayName("findByReservationId는 예약이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_reservation_not_found() {
            var actual = reader.findByReservationId(UNKNOWN_RESERVATION_ID);

            assertThat(actual).isEmpty();
        }
    }
}
