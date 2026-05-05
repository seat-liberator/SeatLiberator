package com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.domain.waitlist.Waitlist;
import com.seatliberator.seatliberator.reservation.domain.waitlist.WaitlistStatus;
import com.seatliberator.seatliberator.reservation.persistence.AbstractPersistenceAdapterTest;
import com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa.repository.WaitlistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaWaitlistStore.class})
@DisplayName("Waitlist Persistence")
public class JpaWaitlistStoreTest extends AbstractPersistenceAdapterTest {
    @Autowired
    WaitlistStore store;

    @Autowired
    WaitlistRepository repository;

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("findById는 대기열 Id에 해당하는 대기열을 반환한다")
        void should_find_waitlist_by_id() {
            var waitlist = saveWaitlist();
            flushAndClear();

            var actual = store.findById(waitlist.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameWaitlist(found, waitlist));
        }

        @Test
        @DisplayName("findById는 대기열 Id에 해당하는 대기열이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_waitlist_not_found_by_id() {
            var actual = store.findById(UUID.randomUUID());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByLocatorAndRange는 Locator가 같고 시간이 겹치는 대기열을 반환한다")
        void should_find_waitlists_by_locator_and_overlapping_range() {
            var waitlist = saveWaitlist();
            saveWaitlist(waitlist(OTHER_USER_ID, locator(ROOM_ID, OTHER_SEAT_ID), reservationRange()));
            saveWaitlist(waitlist(OTHER_USER_ID, locator(), nonOverlappingRange()));
            flushAndClear();

            var actual = store.findByLocatorAndRange(locator(), overlappingRange());

            assertThat(actual)
                    .hasSize(1)
                    .anySatisfy(found -> assertSameWaitlist(found, waitlist));
        }

        @Test
        @DisplayName("findByLocatorAndRangeAndStatus는 Locator, 시간, 상태가 일치하는 대기열을 반환한다")
        void should_find_waitlists_by_locator_range_and_status() {
            var activeWaitlist = saveWaitlist();
            var cancelledWaitlist = saveWaitlist(waitlist(OTHER_USER_ID, locator(), reservationRange()));
            cancelledWaitlist.cancel(reservationStartAt().minusSeconds(30));
            flushAndClear();

            var actual = store.findByLocatorAndRangeAndStatus(locator(), overlappingRange(), WaitlistStatus.ACTIVE);

            assertThat(actual)
                    .hasSize(1)
                    .anySatisfy(found -> assertSameWaitlist(found, activeWaitlist));
        }

        @Test
        @DisplayName("existsByUserIdAndLocatorAndRangeAndStatus는 조건이 모두 일치하면 True")
        void should_return_true_when_exists_waitlist_by_user_locator_range_and_status() {
            saveWaitlist();
            flushAndClear();

            var actual = store.existsByUserIdAndLocatorAndRangeAndStatus(
                    USER_ID,
                    locator(),
                    reservationRange(),
                    WaitlistStatus.ACTIVE
            );

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByUserIdAndLocatorAndRangeAndStatus는 상태가 다르면 False")
        void should_return_false_when_waitlist_status_does_not_match() {
            saveWaitlist();
            flushAndClear();

            var actual = store.existsByUserIdAndLocatorAndRangeAndStatus(
                    USER_ID,
                    locator(),
                    reservationRange(),
                    WaitlistStatus.CANCELLED
            );

            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Store 테스트")
    class StoreTest {
        @Test
        @DisplayName("save는 대기열을 저장한다")
        void should_save_waitlist() {
            var waitlist = waitlist();

            var savedWaitlist = store.save(waitlist);
            flushAndClear();

            var actual = repository.findById(savedWaitlist.getId());
            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameWaitlist(found, savedWaitlist));
        }

        @Test
        @DisplayName("saveAll은 여러 대기열을 저장한다")
        void should_save_all_waitlists() {
            var waitlist = waitlist();
            var otherWaitlist = waitlist(OTHER_USER_ID, locator(ROOM_ID, OTHER_SEAT_ID), reservationRange());

            var savedWaitlists = store.saveAll(List.of(waitlist, otherWaitlist));
            flushAndClear();

            assertThat(savedWaitlists).hasSize(2);
            assertThat(repository.findAll())
                    .hasSize(2)
                    .anySatisfy(found -> assertSameWaitlist(found, waitlist))
                    .anySatisfy(found -> assertSameWaitlist(found, otherWaitlist));
        }
    }

    private Waitlist saveWaitlist() {
        return saveWaitlist(waitlist());
    }

    private Waitlist saveWaitlist(Waitlist waitlist) {
        return repository.save(waitlist);
    }

    private void assertSameWaitlist(Waitlist actual, Waitlist expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getLocator().roomId()).isEqualTo(expected.getLocator().roomId());
        assertThat(actual.getLocator().seatId()).isEqualTo(expected.getLocator().seatId());
        assertThat(actual.getRange().startAt()).isEqualTo(expected.getRange().startAt());
        assertThat(actual.getRange().endAt()).isEqualTo(expected.getRange().endAt());
        assertThat(actual.getBehavior()).isEqualTo(expected.getBehavior());
        assertThat(actual.getState().getStatus()).isEqualTo(expected.getState().getStatus());
        assertThat(actual.getState().getResolution()).isEqualTo(expected.getState().getResolution());
        assertThat(actual.getState().getRequestedAt()).isEqualTo(expected.getState().getRequestedAt());
    }
}
