package com.seatliberator.seatliberator.reservation.persistence.seat.jpa;

import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatReader;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.SeatStore;
import com.seatliberator.seatliberator.reservation.application.seat.port.out.criteria.SeatExclusion;
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

import java.util.List;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.*;
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
        return saveRoom(ROOM_ID);
    }

    private Room saveRoom(String roomId) {
        return roomRepository.save(room(roomId));
    }

    private Seat saveSeat(Room room) {
        return saveSeat(room, SEAT_ID);
    }

    private Seat saveSeat(Room room, String seatId) {
        return seatRepository.save(seat(room, seatId));
    }

    private void assertSameSeat(Seat actual, Seat expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getRoom().getRoomId()).isEqualTo(expected.getRoom().getRoomId());
        assertThat(actual.getSeatId()).isEqualTo(expected.getSeatId());
        assertThat(actual.getStatus()).isEqualTo(expected.getStatus());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("findByLocator는 Locator에 해당하는 좌석을 반환한다")
        void should_find_seat_by_locator() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = reader.findByLocator(seat.getLocator());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, seat));
        }

        @Test
        @DisplayName("findByLocator는 Locator에 해당하는 좌석이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_seat_not_found_by_locator() {
            var actual = reader.findByLocator(locator());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByRoomId는 방 Id에 해당하는 모든 좌석을 반환한다")
        void should_find_seats_by_room_id() {
            var room = saveRoom();
            var otherRoom = saveRoom(OTHER_ROOM_ID);
            var seat = saveSeat(room);
            var otherSeat = saveSeat(room, OTHER_SEAT_ID);
            saveSeat(otherRoom);
            flushAndClear();

            var actual = reader.findByRoomId(room.getRoomId());

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameSeat(found, seat))
                    .anySatisfy(found -> assertSameSeat(found, otherSeat));
        }

        @Test
        @DisplayName("existsByLocator는 Locator에 해당하는 좌석이 있으면 True")
        void should_return_true_when_exists_seat_by_locator() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = reader.existsByLocator(seat.getLocator());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByLocator는 Locator에 해당하는 좌석이 없으면 False")
        void should_return_false_when_seat_not_exists_by_locator() {
            var actual = reader.existsByLocator(locator());

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsByLocator는 제외 대상에 포함된 좌석을 존재 여부에서 제외한다")
        void should_exclude_seat_when_exists_by_locator_with_exclusion() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var exclusion = SeatExclusion.of(List.of(seat.getId()));
            var actual = reader.existsByLocator(seat.getLocator(), exclusion);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsByLocator는 제외 대상에 포함되지 않은 좌석이면 True")
        void should_return_true_when_seat_exists_and_not_excluded() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var otherSeat = saveSeat(room, OTHER_SEAT_ID);
            flushAndClear();

            var exclusion = SeatExclusion.of(List.of(otherSeat.getId()));
            var actual = reader.existsByLocator(seat.getLocator(), exclusion);

            assertThat(actual).isTrue();
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

            store.save(seat);
            flushAndClear();

            var actual = seatRepository.findByRoom_RoomIdAndSeatId(room.getRoomId(), seat.getSeatId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, seat));
        }

        @Test
        @DisplayName("findByRoomIdAndSeatId는 방 Id와 좌석 Id에 해당하는 좌석을 반환한다")
        void should_find_seat_by_room_id_and_seat_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = store.findByRoomIdAndSeatId(room.getRoomId(), seat.getSeatId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, seat));
        }

        @Test
        @DisplayName("findByRoomIdAndSeatId는 방 Id와 좌석 Id에 해당하는 좌석이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_seat_not_found_by_room_id_and_seat_id() {
            var actual = store.findByRoomIdAndSeatId(ROOM_ID, SEAT_ID);

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findForUpdate는 방 Id와 좌석 Id에 해당하는 좌석을 반환한다")
        void should_find_seat_for_update_by_room_id_and_seat_id() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = store.findForUpdate(room.getRoomId(), seat.getSeatId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, seat));
        }

        @Test
        @DisplayName("findForUpdate는 Locator에 해당하는 좌석을 반환한다")
        void should_find_seat_for_update_by_locator() {
            var room = saveRoom();
            var seat = saveSeat(room);
            flushAndClear();

            var actual = store.findForUpdate(seat.getLocator());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameSeat(found, seat));
        }

        @Test
        @DisplayName("deleteByLocator는 Locator에 해당하는 좌석을 삭제한다")
        void should_delete_seat_by_locator() {
            var room = saveRoom();
            var seat = saveSeat(room);
            var otherSeat = saveSeat(room, OTHER_SEAT_ID);
            flushAndClear();

            store.deleteByLocator(seat.getLocator());
            flushAndClear();

            assertThat(seatRepository.existsById(seat.getId())).isFalse();
            assertThat(seatRepository.existsById(otherSeat.getId())).isTrue();
        }
    }
}
