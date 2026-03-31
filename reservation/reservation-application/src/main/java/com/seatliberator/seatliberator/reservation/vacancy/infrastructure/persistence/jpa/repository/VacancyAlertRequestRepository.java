package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.domain.VacancyAlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface VacancyAlertRequestRepository extends JpaRepository<VacancyAlertRequest, UUID> {

    @Query("""
            SELECT COUNT(v) > 0
            FROM VacancyAlertRequest v
            WHERE v.userId = :userId
                AND v.locator.roomId = :targetRoomId
                AND v.locator.seatId = :targetSeatId
                AND v.range.startAt = :targetStartAt
                AND v.range.endAt = :targetEndAt
                AND v.status = :status""")
    boolean existsRequestFor(
            @Param("userId") String userId,
            @Param("targetRoomId") String targetRoomId,
            @Param("targetSeatId") String targetSeatId,
            @Param("targetStartAt") Instant targetStartAt,
            @Param("targetEndAt") Instant targetEndAt,
            @Param("status") VacancyAlertStatus status
    );

    @Query("""
            SELECT v
            FROM VacancyAlertRequest v
            WHERE v.locator.roomId = :targetRoomId
                AND v.locator.seatId = :targetSeatId
                AND v.status = :status
                AND v.range.startAt < :targetEndAt
                AND v.range.endAt > :targetStartAt""")
    List<VacancyAlertRequest> findAllRequestsByRoomAndSeatAndTimeRange(
            @Param("targetRoomId") String targetRoomId,
            @Param("targetSeatId") String targetSeatId,
            @Param("targetStartAt") Instant targetStartAt,
            @Param("targetEndAt") Instant targetEndAt,
            @Param("status") VacancyAlertStatus status
    );
}
