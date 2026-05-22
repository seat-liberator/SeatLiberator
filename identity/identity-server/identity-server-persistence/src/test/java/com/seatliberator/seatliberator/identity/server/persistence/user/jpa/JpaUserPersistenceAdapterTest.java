package com.seatliberator.seatliberator.identity.server.persistence.user.jpa;

import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserReader;
import com.seatliberator.seatliberator.identity.server.application.user.port.out.UserStore;
import com.seatliberator.seatliberator.identity.server.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.identity.server.persistence.user.jpa.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.seatliberator.seatliberator.identity.server.persistence.TestSupport.user;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaUserPersistenceAdapter.class)
@DisplayName("User Persistence")
public class JpaUserPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    UserReader reader;

    @Autowired
    UserStore store;

    @Autowired
    UserRepository repository;

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsById는 사용자 Id에 해당하는 사용자가 있으면 true")
        void should_return_true_when_user_exists_by_id() {
            var user = repository.save(user());
            flushAndClear();

            var actual = reader.existsById(user.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 사용자 Id에 해당하는 사용자가 없으면 false")
        void should_return_false_when_user_does_not_exist_by_id() {
            var user = repository.save(user());
            var userId = user.getId();
            repository.delete(user);
            flushAndClear();

            var actual = reader.existsById(userId);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 사용자 Id에 해당하는 사용자를 반환한다")
        void should_find_user_by_id() {
            var user = repository.save(user());
            flushAndClear();

            var actual = reader.findById(user.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(user);
        }

        @Test
        @DisplayName("findById는 사용자 Id에 해당하는 사용자가 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_user_not_found_by_id() {
            var user = repository.save(user());
            var userId = user.getId();
            repository.delete(user);
            flushAndClear();

            var actual = reader.findById(userId);

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 사용자를 저장한다")
        void should_save_user() {
            var user = user();

            var saved = store.save(user);
            flushAndClear();

            var actual = repository.findById(saved.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(saved);
        }

        @Test
        @DisplayName("delete는 사용자를 삭제한다")
        void should_delete_user() {
            var user = repository.save(user());
            flushAndClear();

            store.delete(user);
            flushAndClear();

            assertThat(repository.existsById(user.getId())).isFalse();
        }
    }
}
