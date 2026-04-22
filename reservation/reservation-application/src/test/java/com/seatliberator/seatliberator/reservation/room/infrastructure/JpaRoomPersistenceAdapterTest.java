package com.seatliberator.seatliberator.reservation.room.infrastructure;

import com.seatliberator.seatliberator.reservation.domain.persistence.Room;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomReader;
import com.seatliberator.seatliberator.reservation.room.application.port.out.RoomStore;
import com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa.JpaRoomPersistenceAdapter;
import com.seatliberator.seatliberator.reservation.room.infrastructure.persistence.jpa.repository.RoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.Clock;
import java.time.Instant;

import static com.seatliberator.seatliberator.reservation.domain.fixture.TestSupport.fixedClock;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaRoomPersistenceAdapter.class})
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Room Persistence")
public class JpaRoomPersistenceAdapterTest {
    @Autowired
    RoomRepository repository;

    @Autowired
    RoomReader reader;

    @Autowired
    RoomStore store;

    Clock clock = fixedClock;

    Instant now = clock.instant();

    @Test
    @DisplayName("save는 Room을 저장하고 저장된 Room을 반환한다")
    void save() {
        var roomId = "study-room-1";
        var room = Room.of(roomId, now);

        var saved = store.save(room);

        assertThat(saved.getRoomId()).isEqualTo(roomId);
        assertThat(reader.findByRoomId(roomId)).isPresent();
    }

    @Test
    @DisplayName("store.deleteByRoomId는 roomId에 해당하는 Room을 삭제한다")
    void delete_by_roomId() {
        var roomId = "study-room-1";
        repository.saveAndFlush(Room.of(roomId, now));

        store.deleteByRoomId(roomId);
        assertThat(repository.existsByRoomId(roomId)).isFalse();
    }

    @Test
    @DisplayName("reader.findByRoomId는 roomId에 해당하는 Room을 반환한다")
    void find_by_roomId() {
        var roomId = "study-room-1";
        repository.saveAndFlush(Room.of(roomId, now));

        var found = reader.findByRoomId(roomId);

        assertThat(found).isPresent();
        assertThat(found.get().getRoomId()).isEqualTo(roomId);
    }

    @Test
    @DisplayName("reader.existsByRoomId는 roomId에 해당하는 Room 존재 여부를 반환한다")
    void exists_by_roomId() {
        var roomId = "study-room-1";
        repository.saveAndFlush(Room.of(roomId, now));

        var exists = reader.existsByRoomId(roomId);

        assertThat(exists).isTrue();
    }
}