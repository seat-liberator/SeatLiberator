package com.seatliberator.seatliberator.reservation.persistence.seat.jpa;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.filter.SeatFilter;
import com.seatliberator.seatliberator.reservation.domain.room.Room;
import com.seatliberator.seatliberator.reservation.domain.seat.Seat;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import com.seatliberator.seatliberator.reservation.persistence.seat.jpa.repository.SeatRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.seatliberator.seatliberator.reservation.persistence.seat.SeatTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaSeatPersistenceAdapter.class})
@DisplayName("Seat Persistence")
public class JpaSeatPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    SeatReader reader;

    @Autowired
    SeatStore store;

    @Autowired
    SeatRepository seatRepository;

    @Autowired
    RoomRepository roomRepository;

    private Room saveRoom() {
        return roomRepository.save(room());
    }

    private Room saveOtherRoom() {
        return roomRepository.save(otherRoom());
    }

    private Seat saveSeat(Room room) {
        return seatRepository.save(seat(room));
    }

    private Seat saveOtherSeat(Room room) {
        return seatRepository.save(otherSeat(room));
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsById는 좌석 Id에 해당하는 좌석이 있으면 True")
        void should_return_true_when_exists_seat_by_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = reader.existsById(seat.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 좌석 Id에 해당하는 좌석이 없으면 False")
        void should_return_false_when_seat_not_exists_by_id() {
            var actual = reader.existsById(UNKNOWN_SEAT_ID);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsByCriteria는 조건에 해당하는 좌석이 있으면 True")
        void should_return_true_when_exists_seat_by_criteria() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = reader.existsByCriteria(lookupCriteria(seat));

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByCriteria는 조건에 해당하는 좌석이 없으면 False")
        void should_return_false_when_seat_not_exists_by_criteria() {
            var room = saveRoom();
            flushAndClear();

            var actual = reader.existsByCriteria(unknownSeatLookupCriteria(room));

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 좌석 Id에 해당하는 좌석을 반환한다")
        void should_find_seat_by_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = reader.findById(seat.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, seat));
        }

        @Test
        @DisplayName("findById는 좌석 Id에 해당하는 좌석이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_seat_not_found_by_id() {
            var actual = reader.findById(UNKNOWN_SEAT_ID);

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByFilter는 방 Id에 해당하는 모든 좌석을 반환한다")
        void should_find_seats_by_room_id_filter() {
            var room = saveRoom();
            var otherRoom = saveOtherRoom();
            var seat = saveSeat(room);
            var otherSeat = saveOtherSeat(room);
            saveSeat(otherRoom);
            flushAndClear();

            var actual = reader.findByFilter(roomSeatFilter(room));

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameSeat(found, seat))
                    .anySatisfy(found -> assertSameSeat(found, otherSeat));
        }

        @Test
        @DisplayName("findByFilter는 빈 필터이면 저장된 모든 좌석을 반환한다")
        void should_find_all_seats_when_filter_empty() {
            var room = saveRoom();
            var otherRoom = saveOtherRoom();
            var seat = saveSeat(room);
            var otherSeat = saveSeat(otherRoom);
            flushAndClear();

            var actual = reader.findByFilter(SeatFilter.empty());

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameSeat(found, seat))
                    .anySatisfy(found -> assertSameSeat(found, otherSeat));
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 좌석을 저장한다")
        void should_save_seat() {
            var room = saveRoom();
            var seat = seat(room);

            var saved = store.save(seat);
            flushAndClear();

            var actual = seatRepository.findById(saved.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, saved));
        }

        @Test
        @DisplayName("delete는 좌석을 삭제한다")
        void should_delete_seat() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var otherSeat = saveOtherSeat(room);
            flushAndClear();

            store.delete(seat);
            flushAndClear();

            assertThat(seatRepository.existsById(seat.getId())).isFalse();
            assertThat(seatRepository.existsById(otherSeat.getId())).isTrue();
        }
    }
}
