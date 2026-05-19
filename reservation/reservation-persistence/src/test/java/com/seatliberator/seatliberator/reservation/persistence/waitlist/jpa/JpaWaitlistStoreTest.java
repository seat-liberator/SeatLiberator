package com.seatliberator.seatliberator.reservation.persistence.waitlist.jpa;

import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistReader;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.WaitlistStore;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistFilter;
import com.seatliberator.seatliberator.reservation.application.waitlist.port.out.filter.WaitlistOrder;
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
import java.util.Set;
import java.util.UUID;

import static com.seatliberator.seatliberator.reservation.persistence.TestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaWaitlistStore.class})
@DisplayName("Waitlist Persistence")
public class JpaWaitlistStoreTest extends AbstractPersistenceAdapterTest {
    @Autowired
    WaitlistReader reader;

    @Autowired
    WaitlistStore store;

    @Autowired
    WaitlistRepository repository;

    private void assertSameWaitlist(Waitlist actual, Waitlist expected) {
        assertThat(actual.getId()).isEqualTo(expected.getId());
        assertThat(actual.getUserId()).isEqualTo(expected.getUserId());
        assertThat(actual.getSlotIds()).containsExactlyElementsOf(expected.getSlotIds());
        assertThat(actual.getOccupancyDate()).isEqualTo(expected.getOccupancyDate());
        assertThat(actual.getBehavior()).isEqualTo(expected.getBehavior());
        assertThat(actual.getState().getStatus()).isEqualTo(expected.getState().getStatus());
        assertThat(actual.getState().getResolution()).isEqualTo(expected.getState().getResolution());
        assertThat(actual.getState().getRequestedAt()).isEqualTo(expected.getState().getRequestedAt());
        assertThat(actual.getState().getCancelledAt()).isEqualTo(expected.getState().getCancelledAt());
        assertThat(actual.getState().getExpiredAt()).isEqualTo(expected.getState().getExpiredAt());
        assertThat(actual.getState().getCompletedAt()).isEqualTo(expected.getState().getCompletedAt());
    }

    @Nested
    @DisplayName("Reader 테스트")
    class ReaderTest {
        @Test
        @DisplayName("existsById는 대기열 Id에 해당하는 대기열이 있으면 True")
        void should_return_true_when_exists_waitlist_by_id() {
            var waitlist = saveWaitlist();
            flushAndClear();

            var actual = reader.existsById(waitlist.getId());

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsById는 대기열 Id에 해당하는 대기열이 없으면 False")
        void should_return_false_when_waitlist_not_exists_by_id() {
            var actual = reader.existsById(UUID.randomUUID());

            assertThat(actual).isFalse();
        }

        @Test
        @DisplayName("findById는 대기열 Id에 해당하는 대기열을 반환한다")
        void should_find_waitlist_by_id() {
            var waitlist = saveWaitlist();
            flushAndClear();

            var actual = reader.findById(waitlist.getId());

            assertThat(actual)
                    .isPresent()
                    .get()
                    .satisfies(found -> assertSameWaitlist(found, waitlist));
        }

        @Test
        @DisplayName("findById는 대기열 Id에 해당하는 대기열이 없으면 Optional.empty를 반환한다")
        void should_return_empty_when_waitlist_not_found_by_id() {
            var actual = reader.findById(UUID.randomUUID());

            assertThat(actual).isEmpty();
        }

        @Test
        @DisplayName("findByFilter는 슬롯 Id와 점유일이 일치하는 대기열을 반환한다")
        void should_find_waitlists_by_slot_ids_and_occupancy_date_filter() {
            var waitlist = saveWaitlist();
            saveWaitlist(waitlist(OTHER_USER_ID, List.of(OTHER_SLOT_ID), occupancyDate()));
            saveWaitlist(waitlist(OTHER_USER_ID, List.of(SLOT_ID), occupancyDate().plusDays(1)));
            flushAndClear();

            var filter = WaitlistFilter.empty()
                    .slotIds(Set.of(SLOT_ID))
                    .occupancyDate(occupancyDate());
            var actual = reader.findByFilter(filter, WaitlistOrder.fifo());

            assertThat(actual)
                    .hasSize(1)
                    .anySatisfy(found -> assertSameWaitlist(found, waitlist));
        }

        @Test
        @DisplayName("findByFilter는 슬롯 Id와 점유일과 상태가 일치하는 대기열을 반환한다")
        void should_find_waitlists_by_slot_ids_occupancy_date_and_status_filter() {
            var activeWaitlist = saveWaitlist();
            var cancelledWaitlist = saveWaitlist(waitlist(OTHER_USER_ID, List.of(SLOT_ID), occupancyDate()));
            cancelledWaitlist.cancel(reservationStartAt());
            flushAndClear();

            var filter = WaitlistFilter.empty()
                    .slotIds(Set.of(SLOT_ID))
                    .occupancyDate(occupancyDate())
                    .status(WaitlistStatus.ACTIVE);
            var actual = reader.findByFilter(filter, WaitlistOrder.fifo());

            assertThat(actual)
                    .hasSize(1)
                    .anySatisfy(found -> assertSameWaitlist(found, activeWaitlist));
        }

        @Test
        @DisplayName("existsByFilter는 조건이 모두 일치하면 True")
        void should_return_true_when_exists_waitlist_by_filter() {
            saveWaitlist();
            flushAndClear();

            var filter = WaitlistFilter.empty()
                    .userId(USER_ID)
                    .slotIds(Set.of(SLOT_ID))
                    .occupancyDate(occupancyDate())
                    .status(WaitlistStatus.ACTIVE);
            var actual = reader.existsByFilter(filter);

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("existsByFilter는 상태가 다르면 False")
        void should_return_false_when_waitlist_status_does_not_match() {
            saveWaitlist();
            flushAndClear();

            var filter = WaitlistFilter.empty()
                    .userId(USER_ID)
                    .slotIds(Set.of(SLOT_ID))
                    .occupancyDate(occupancyDate())
                    .status(WaitlistStatus.CANCELLED);
            var actual = reader.existsByFilter(filter);

            assertThat(actual).isFalse();
        }
    }

    private Waitlist saveWaitlist() {
        return saveWaitlist(waitlist());
    }

    private Waitlist saveWaitlist(Waitlist waitlist) {
        return repository.save(waitlist);
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
            var otherWaitlist = waitlist(OTHER_USER_ID, List.of(OTHER_SLOT_ID), occupancyDate());

            var savedWaitlists = store.saveAll(List.of(waitlist, otherWaitlist));
            flushAndClear();

            assertThat(savedWaitlists).hasSize(2);
            assertThat(repository.findAll())
                    .hasSize(2)
                    .anySatisfy(found -> assertSameWaitlist(found, waitlist))
                    .anySatisfy(found -> assertSameWaitlist(found, otherWaitlist));
        }
    }
}
