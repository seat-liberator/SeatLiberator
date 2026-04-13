package com.seatliberator.seatliberator.reservation.vacancy.application.internal;

import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationPolicyChecker;
import com.seatliberator.seatliberator.reservation.book.application.contract.result.ReservationRejectReason;
import com.seatliberator.seatliberator.reservation.book.application.port.in.CreateReservationUseCase;
import com.seatliberator.seatliberator.reservation.book.application.port.in.command.CreateReservationCommand;
import com.seatliberator.seatliberator.reservation.domain.SeatLocator;
import com.seatliberator.seatliberator.reservation.domain.TimeRange;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacancyAlertRequestPromotion {
    private final ReservationPolicyChecker policyChecker;
    private final CreateReservationUseCase createReservationUseCase;

    public VacancyAlertRequestPromotionResult promote(String userId, SeatLocator locator, TimeRange range) {
        var policyCheckResult = policyChecker.check(userId, locator, range);

        if (!policyCheckResult.reservable()) {
            var failReason = Optional.ofNullable(policyCheckResult.rejectReason()).map(ReservationRejectReason::message).orElse("예약 정책 위반");
            return VacancyAlertRequestPromotionResult.fail(failReason);
        }

        try {
            var command = CreateReservationCommand.of(userId, locator, range);
            createReservationUseCase.create(command);
            return VacancyAlertRequestPromotionResult.success();
        } catch (Exception exception) {
            return VacancyAlertRequestPromotionResult.fail("예약 실패");
        }
    }
}
