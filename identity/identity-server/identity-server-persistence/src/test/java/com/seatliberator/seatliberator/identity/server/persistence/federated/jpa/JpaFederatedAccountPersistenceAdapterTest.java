package com.seatliberator.seatliberator.identity.server.persistence.federated.jpa;

import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountReader;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.FederatedAccountStore;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountLookupCriteria;
import com.seatliberator.seatliberator.identity.server.application.federated.port.out.criteria.FederatedAccountUserRegistrationLookupCriteria;
import com.seatliberator.seatliberator.identity.server.domain.account.FederatedAccount;
import com.seatliberator.seatliberator.identity.server.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.identity.server.persistence.TestSupport;
import com.seatliberator.seatliberator.identity.server.persistence.federated.jpa.repository.FederatedAccountRepository;
import com.seatliberator.seatliberator.identity.server.persistence.user.jpa.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static com.seatliberator.seatliberator.identity.server.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaFederatedAccountPersistenceAdapter.class)
@DisplayName("FederatedAccount Persistence")
public class JpaFederatedAccountPersistenceAdapterTest extends AbstractPersistenceAdapterTest {
    @Autowired
    FederatedAccountReader reader;

    @Autowired
    FederatedAccountStore store;

    @Autowired
    FederatedAccountRepository repository;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsById는 federated 계정 Id에 해당하는 계정이 있으면 true")
        void should_return_true_when_account_exists_by_id() {
            var account = repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.existsById(account.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 federated 계정 Id에 해당하는 계정이 없으면 false")
        void should_return_false_when_account_does_not_exist_by_id() {
            var account = repository.save(federatedAccount());
            var accountId = account.getId();
            repository.delete(account);
            flushAndClear();

            var actual = reader.existsById(accountId);

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("existsByCriteria는 registrationId와 providerUserId에 해당하는 federated 계정이 있으면 true")
        void should_return_true_when_account_exists_by_registration_and_provider_user() {
            repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID));

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByCriteria는 registrationId와 providerUserId에 해당하는 federated 계정이 없으면 false")
        void should_return_false_when_account_does_not_exist_by_registration_and_provider_user() {
            repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.existsByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, OTHER_PROVIDER_USER_ID));

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 federated 계정 Id에 해당하는 계정을 반환한다")
        void should_find_account_by_id() {
            var account = repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.findById(account.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertAccountEquals(found, account));
        }

        @Test
        @DisplayName("findById는 federated 계정 Id에 해당하는 계정이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_account_not_found_by_id() {
            var account = repository.save(federatedAccount());
            var accountId = account.getId();
            repository.delete(account);
            flushAndClear();

            var actual = reader.findById(accountId);

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByCriteria는 registrationId와 providerUserId에 해당하는 federated 계정을 반환한다")
        void should_find_account_by_registration_and_provider_user() {
            var account = repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.findByCriteria(FederatedAccountLookupCriteria.of(REGISTRATION_ID, PROVIDER_USER_ID));

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertAccountEquals(found, account));
        }

        @Test
        @DisplayName("findByCriteria는 userId와 registrationId에 해당하는 federated 계정을 반환한다")
        void should_find_account_by_user_id_and_registration_id() {
            var account = repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.findByCriteria(
                    FederatedAccountUserRegistrationLookupCriteria.of(account.getUserId(), REGISTRATION_ID)
            );

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertAccountEquals(found, account));
        }

        @Test
        @DisplayName("findByCriteria는 userId와 registrationId에 해당하는 federated 계정이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_account_not_found_by_user_id_and_registration_id() {
            var account = repository.save(federatedAccount());
            flushAndClear();

            var actual = reader.findByCriteria(
                    FederatedAccountUserRegistrationLookupCriteria.of(account.getUserId(), OTHER_REGISTRATION_ID)
            );

            assertThat(actual).isEmpty();
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 federated 계정을 저장한다")
        void should_save_account() {
            var account = federatedAccount();

            var saved = store.save(account);
            flushAndClear();

            var actual = repository.findById(saved.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertAccountEquals(found, saved));
        }

        @Test
        @DisplayName("delete는 federated 계정을 삭제한다")
        void should_delete_account() {
            var account = repository.save(federatedAccount());
            flushAndClear();

            store.delete(account);
            flushAndClear();

            assertThat(repository.existsById(account.getId())).isFalse();
        }
    }

    private FederatedAccount federatedAccount() {
        var user = userRepository.save(user());
        return TestSupport.federatedAccount(user.getId());
    }

    private void assertAccountEquals(FederatedAccount actual, FederatedAccount expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getRegistrationId()).isEqualTo(expected.getRegistrationId());
        assertThat(actual.getProviderUserId()).isEqualTo(expected.getProviderUserId());
        assertThat(actual.getCreatedAt()).isEqualTo(expected.getCreatedAt());
    }
}
