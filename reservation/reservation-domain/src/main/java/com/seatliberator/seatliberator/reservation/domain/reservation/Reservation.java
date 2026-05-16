package com.seatliberator.seatliberator.reservation.domain.reservation;

import com.seatliberator.seatliberator.kernel.condition.Preconditions;
import com.seatliberator.seatliberator.reservation.domain.reservation.event.DomainEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Transient
    private final List<DomainEvent> events = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    @Embedded
    private ReservationState state;

    private Reservation(String userId, ReservationState state) {
        this.userId = Preconditions.requireNonBlank(userId, "userId");
        this.state = Preconditions.requireNonNull(state, "state");
    }

    public static Reservation of(String userId, Instant reservedAt) {
        return new Reservation(userId, ReservationState.reservedAt(reservedAt));
    }

    @AfterDomainEventPublication
    private void afterDomainEventPublication() {
        this.events.clear();
    }

    @DomainEvents
    Collection<Object> domainEvents() {
        return Collections.unmodifiableList(events);
    }

    public void use(Instant usedAt) {
        this.state.use(usedAt);
    }

    public void expire(Instant expiredAt) {
        this.state.expire(expiredAt);
    }

    public void cancel(Instant canceledAt) {
        this.state.cancel(canceledAt);
    }
}
