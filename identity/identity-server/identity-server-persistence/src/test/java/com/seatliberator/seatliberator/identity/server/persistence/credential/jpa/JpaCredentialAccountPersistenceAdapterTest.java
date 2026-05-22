package com.seatliberator.seatliberator.identity.server.persistence.credential.jpa;

import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountReader;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.CredentialAccountStore;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountEmailCriteria;
import com.seatliberator.seatliberator.identity.server.application.credential.port.out.criteria.CredentialAccountUserCriteria;
import com.seatliberator.seatliberator.identity.server.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.identity.server.persistence.credential.jpa.repository.CredentialAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.seatliberator.seatliberator.identity.server.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaCredentialAccountPersistenceAdapter.class)
@DisplayName("CredentialAccount Persistence")
public class JpaCredentialAccountPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    CredentialAccountReader reader;

    @Autowired
    CredentialAccountStore store;

    @Autowired
    CredentialAccountRepository repository;

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsById는 credential 계정 Id에 해당하는 계정이 있으면 true")
        void should_return_true_when_account_exists_by_id() {
            var account = repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.existsById(account.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 credential 계정 Id에 해당하는 계정이 없으면 false")
        void should_return_false_when_account_does_not_exist_by_id() {
            var account = repository.save(credentialAccount());
            var accountId = account.getId();
            repository.delete(account);
            flushAndClear();

            var actual = reader.existsById(accountId);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsByCriteria는 email에 해당하는 credential 계정이 있으면 true")
        void should_return_true_when_account_exists_by_email() {
            repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.existsByCriteria(CredentialAccountEmailCriteria.of(EMAIL));

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByCriteria는 email에 해당하는 credential 계정이 없으면 false")
        void should_return_false_when_account_does_not_exist_by_email() {
            repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.existsByCriteria(CredentialAccountEmailCriteria.of(OTHER_EMAIL));

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 credential 계정 Id에 해당하는 계정을 반환한다")
        void should_find_account_by_id() {
            var account = repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.findById(account.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(account);
        }

        @Test
        @DisplayName("findById는 credential 계정 Id에 해당하는 계정이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_account_not_found_by_id() {
            var account = repository.save(credentialAccount());
            var accountId = account.getId();
            repository.delete(account);
            flushAndClear();

            var actual = reader.findById(accountId);

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByCriteria는 email에 해당하는 credential 계정을 반환한다")
        void should_find_account_by_email() {
            var account = repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.findByCriteria(CredentialAccountEmailCriteria.of(EMAIL));

            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(account);
        }

        @Test
        @DisplayName("findByCriteria는 userId에 해당하는 credential 계정을 반환한다")
        void should_find_account_by_user_id() {
            var account = repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.findByCriteria(CredentialAccountUserCriteria.of(USER_ID));

            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(account);
        }

        @Test
        @DisplayName("findByCriteria는 userId에 해당하는 credential 계정이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_account_not_found_by_user_id() {
            repository.save(credentialAccount());
            flushAndClear();

            var actual = reader.findByCriteria(CredentialAccountUserCriteria.of(OTHER_USER_ID));

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 credential 계정을 저장한다")
        void should_save_account() {
            var account = credentialAccount();

            var saved = store.save(account);
            flushAndClear();

            var actual = repository.findById(saved.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .usingRecursiveComparison()
                    .isEqualTo(saved);
        }

        @Test
        @DisplayName("delete는 credential 계정을 삭제한다")
        void should_delete_account() {
            var account = repository.save(credentialAccount());
            flushAndClear();

            store.delete(account);
            flushAndClear();

            assertThat(repository.existsById(account.getId())).isFalse();
        }
    }
}
