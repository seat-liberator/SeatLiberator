package com.seatliberator.seatliberator.reservation.book.domain;

import com.seatliberator.seatliberator.reservation.book.application.event.payload.BookDomainEventPayload;
import com.seatliberator.seatliberator.reservation.shared.domain.EmbeddableSeatLocator;
import com.seatliberator.seatliberator.reservation.shared.domain.EmbeddableTimeRange;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.DomainEvents;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Transient
    private final List<BookDomainEventPayload> events = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    @Embedded
    private EmbeddableSeatLocator locator;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "target_start_at")),
            @AttributeOverride(name = "endAt", column = @Column(name = "target_end_at"))
    })
    private EmbeddableTimeRange range;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    private Reservation(String userId, EmbeddableSeatLocator locator, EmbeddableTimeRange range, ReservationStatus status) {
        this.userId = userId;
        this.locator = locator;
        this.range = range;
        this.status = status;
    }

    public static Reservation create(String userId, String roomId, String seatId, Instant startAt, Instant endAt) {
        var locator = EmbeddableSeatLocator.from(roomId, seatId);
        var range = EmbeddableTimeRange.from(startAt, endAt);
        return new Reservation(userId, locator, range, ReservationStatus.RESERVED);
    }

    public static Reservation of(String userId, String roomId, String seatId, Instant startAt, Instant endAt, ReservationStatus status) {
        var locator = EmbeddableSeatLocator.from(roomId, seatId);
        var range = EmbeddableTimeRange.from(startAt, endAt);
        return new Reservation(userId, locator, range, status);
    }

    @DomainEvents
    private Collection<Object> event() {
        return Collections.unmodifiableList(events);
    }

    public void update(String userId, String roomId, String seatId, Instant startAt, Instant endAt) {
        this.userId = userId;
        this.locator.setLocate(roomId, seatId);
        this.range.setRange(startAt, endAt);
    }

    public boolean isReserved() {
        return status == ReservationStatus.RESERVED;
    }

    public boolean isUsed() {
        return status == ReservationStatus.USED;
    }

    public boolean isExpired() {
        return status == ReservationStatus.EXPIRED;
    }

    public boolean isCanceled() {
        return status == ReservationStatus.CANCELED;
    }

    public void use(Instant usedAt) {
        expireIfEnded(usedAt);
        ensureUsableAt(usedAt);
        status = ReservationStatus.USED;
    }

    public void cancel(Instant canceledAt) {
        expireIfEnded(canceledAt);
        ensureCancelableAt();
        status = ReservationStatus.CANCELED;
    }

    private void ensureUsableAt(Instant at) {
        ensureStateIn(ReservationStatus.RESERVED);
        if (!range.contains(at)) throw new IllegalArgumentException("사용 가능한 시간이 아닙니다.");
    }

    private void ensureCancelableAt() {
        ensureStateIn(ReservationStatus.RESERVED, ReservationStatus.USED);
    }

    private void expireIfEnded(Instant at) {
        if (range.isEnded(at) && status != ReservationStatus.EXPIRED) status = ReservationStatus.EXPIRED;
    }

    private void ensureStateIn(ReservationStatus... allowed) {
        for (var status : allowed) if (this.status == status) return;

        switch (status) {
            case RESERVED -> throw new IllegalStateException("이미 예약되었습니다.");
            case USED -> throw new IllegalStateException("이미 사용된 예약입니다.");
            case EXPIRED -> throw new IllegalStateException("이미 만료된 예약입니다.");
            case CANCELED -> throw new IllegalStateException("이미 취소된 예약입니다.");
        }
    }
}