package com.seatliberator.seatliberator.reservation.persistence.room.jpa;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.domain.room.RoomFixture;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.OTHER_ROOM_ID;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaRoomPersistenceAdapter.class})
@DisplayName("Room Persistence")
public class JpaRoomPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    RoomReader reader;

    @Autowired
    RoomStore store;

    @Autowired
    RoomRepository repository;

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsByRoomId는 방 Id에 해당하는 방 있으면 True")
        void should_return_true_when_exists_room() {
            var room = RoomFixture.get();
            repository.save(room);
            flushAndClear();

            var roomId = room.getRoomId();

            assertThat(reader.existsByRoomId(roomId)).isTrue();
        }

        @Test
        @DisplayName("existsByRoomId는 방 Id 에 해당하는 방 없으면 False")
        void should_return_false_when_non_exists_room() {
            var room = RoomFixture.get();
            var roomId = room.getRoomId();

            assertThat(reader.existsByRoomId(roomId)).isFalse();
        }

        @Test
        @DisplayName("findByRoomId는 방 Id에 해당하는 방을 반환한다")
        void should_find_room_by_room_id() {
            var room = RoomFixture.get();
            repository.save(room);
            flushAndClear();

            var actual = reader.findByRoomId(room.getRoomId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(room);
        }

        @Test
        @DisplayName("findByRoomId는 방 Id에 해당하는 방이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_room_not_found_by_room_id() {
            var room = RoomFixture.get();

            var actual = reader.findByRoomId(room.getRoomId());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findAll은 저장된 모든 방을 반환한다")
        void should_find_all_rooms() {
            var room = RoomFixture.get();
            var otherRoom = new RoomFixture.Builder()
                    .roomId(OTHER_ROOM_ID)
                    .build();
            repository.save(room);
            repository.save(otherRoom);
            flushAndClear();

            var actual = reader.findAll();

            assertThat(actual)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrder(room, otherRoom);
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 방을 저장한다")
        void should_save_room() {
            var room = RoomFixture.get();

            var savedRoom = store.save(room);
            flushAndClear();

            var actual = repository.findByRoomId(savedRoom.getRoomId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(savedRoom);
        }

        @Test
        @DisplayName("deleteByRoomId는 방 Id에 해당하는 방을 삭제한다")
        void should_delete_room_by_room_id() {
            var room = RoomFixture.get();
            repository.save(room);
            flushAndClear();

            store.deleteByRoomId(room.getRoomId());
            flushAndClear();

            assertThat(repository.existsByRoomId(room.getRoomId())).isFalse();
        }
    }
}
