package com.seatliberator.seatliberator.reservation.application.reservation.service;

import com.seatliberator.seatliberator.reservation.application.reservation.port.in.ListMyReservationUseCase;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.query.ListMyReservationQuery;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.out.ReservationReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListMyReservationService implements ListMyReservationUseCase {
    private final ReservationReader reader;

    @Override
    public List<ReservationResult> list(ListMyReservationQuery query) {
        return reader.findByUserId(query.userId()).stream()
                .map(ReservationResult::from)
                .toList();
    }
}
