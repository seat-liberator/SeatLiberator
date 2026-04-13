package com.seatliberator.seatliberator.reservation.book.application.contract.service;

import com.seatliberator.seatliberator.reservation.book.application.contract.ReservationOwnershipChecker;
import com.seatliberator.seatliberator.reservation.book.application.port.out.ReservationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultReservationOwnershipChecker implements ReservationOwnershipChecker {
    private final ReservationReader reader;

    @Override
    public boolean hasOwnership(Long reservationId, String userId) {
        // 아마 ReservationStore.findByUserId(String userId)를 사용해도 될 것 같음
        // 그 경우 Long reservationId가 사용되지 않는데,
        // 코드 상 명시적으로 예약 Id가 reservationId인 예약에 대해서,
        // 예약자 Id가 userId와 동일하다는 것을 보장하도록 작성함

        var optReservation = reader.findById(reservationId);

        if (optReservation.isEmpty()) return false;

        var reservation = optReservation.get();

        return reservation.getUserId().equals(userId);
    }
}
