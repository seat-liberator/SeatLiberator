package com.seatliberator.seatliberator.reservation.persistence.room.jpa;

import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.application.room.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.room.jpa.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.seatliberator.seatliberator.reservation.persistence.room.RoomTestSupport.*;
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
        @DisplayName("existsById는 방 Id에 해당하는 방이 있으면 True")
        void should_return_true_when_exists_room() {
            var room = room();
            repository.save(room);
            flushAndClear();

            var actual = reader.existsById(room.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 방 Id에 해당하는 방이 없으면 False")
        void should_return_false_when_non_exists_room() {
            var actual = reader.existsById(UNKNOWN_ROOM_ID);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsByCode는 방 코드에 해당하는 방이 있으면 True")
        void should_return_true_when_exists_room_by_code() {
            var room = room();
            repository.save(room);
            flushAndClear();

            var actual = reader.existsByCode(room.getCode());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByCode는 방 코드에 해당하는 방이 없으면 False")
        void should_return_false_when_non_exists_room_by_code() {
            var actual = reader.existsByCode(UNKNOWN_ROOM_CODE);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 방 Id에 해당하는 방을 반환한다")
        void should_find_room_by_id() {
            var room = room();
            repository.save(room);
            flushAndClear();

            var actual = reader.findById(room.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameRoom(found, room));
        }

        @Test
        @DisplayName("findById는 방 Id에 해당하는 방이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_room_not_found_by_id() {
            var actual = reader.findById(UNKNOWN_ROOM_ID);

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findAll은 저장된 모든 방을 반환한다")
        void should_find_all_rooms() {
            var room = room();
            var otherRoom = otherRoom();
            repository.save(room);
            repository.save(otherRoom);
            flushAndClear();

            var actual = reader.findAll();

            assertThat(actual)
                    .hasSize(2)
                    .anySatisfy(found -> assertSameRoom(found, room))
                    .anySatisfy(found -> assertSameRoom(found, otherRoom));
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 방을 저장한다")
        void should_save_room() {
            var room = room();

            var savedRoom = store.save(room);
            flushAndClear();

            var actual = repository.findById(savedRoom.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameRoom(found, savedRoom));
        }

        @Test
        @DisplayName("delete는 방을 삭제한다")
        void should_delete_room() {
            var room = room();
            repository.save(room);
            flushAndClear();

            store.delete(room);
            flushAndClear();

            assertThat(repository.existsById(room.getId())).isFalse();
        }
    }
}
