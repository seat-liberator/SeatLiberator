package com.seatliberator.seatliberator.reservation.vacancy.infrastructure.persistence.jpa.repository;

import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertRequest;
import com.seatliberator.seatliberator.reservation.vacancy.domain.VacancyAlertStatus;
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
                        AND v.roomId = :roomId
                AND v.seatId = :seatId
                AND v.targetStartTime = :targetStartTime
                AND v.targetEndTime = :targetEndTime
                AND v.status = :status""")
    boolean existsRequestFor(
            @Param("userId") String userId,
            @Param("roomId") String roomId,
            @Param("seatId") String seatId,
            @Param("targetStartTime") Instant targetStartTime,
            @Param("targetEndTime") Instant targetEndTime,
            @Param("status") VacancyAlertStatus status
    );

    @Query("""
            SELECT v
            FROM VacancyAlertRequest v
            WHERE v.seatId = :seatId
                AND v.status = :status
                AND v.targetStartTime <= :endTime
                AND v.targetEndTime > :startTime""")
    List<VacancyAlertRequest> findAllRequestsBySeatAndTimeRange(
            @Param("seatId") String seatId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("status") VacancyAlertStatus status
    );
}
