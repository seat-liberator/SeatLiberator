package com.seatliberator.seatliberator.reservation.vacancy.application.service;

import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationManager;
import com.seatliberator.seatliberator.reservation.book.application.port.in.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.ReservationCreateCommand;
import com.seatliberator.seatliberator.reservation.book.application.port.in.entry.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.VacancyAlertRequestPromotion;
import com.seatliberator.seatliberator.reservation.vacancy.application.port.in.entry.VacancyAlertRequestPromotionResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultVacancyAlertRequestPromotion implements VacancyAlertRequestPromotion {
    private final ReservationPolicyChecker policyChecker;
    private final ReservationManager reservationManager;

    public VacancyAlertRequestPromotionResult promote(String userId, SeatLocator locator, TimeRange range) {
        var policyCheckResult = policyChecker.check(userId, locator, range);

        if (!policyCheckResult.reservable()) {
            var failReason = Optional.ofNullable(policyCheckResult.rejectReason()).map(ReservationRejectReason::message).orElse("예약 정책 위반");
            return VacancyAlertRequestPromotionResult.fail(failReason);
        }

        try {
            var command = ReservationCreateCommand.of(userId, locator, range);
            reservationManager.create(command);
            return VacancyAlertRequestPromotionResult.success();
        } catch (Exception exception) {
            return VacancyAlertRequestPromotionResult.fail("예약 실패");
        }
    }
}
