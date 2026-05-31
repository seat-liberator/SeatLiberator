package com.seatliberator.seatliberator.reservation.web.booking.controller;

import com.seatliberator.seatliberator.identity.core.actor.context.ActorContextHolder;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CancelBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.CreateBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.FindBookingUseCase;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.query.FindBookingQuery;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingDetailResult;
import com.seatliberator.seatliberator.reservation.application.booking.port.in.result.BookingResult;
import com.seatliberator.seatliberator.reservation.application.reservation.port.in.result.ReservationResult;
import com.seatliberator.seatliberator.reservation.web.booking.request.CancelBookingRequest;
import com.seatliberator.seatliberator.reservation.web.booking.request.CreateBookingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Booking", description = "스터디룸 좌석 예약 관련 API")
@RequestMapping("/api/v1/booking")
@RestController
@RequiredArgsConstructor
public class BookingController {

    private final FindBookingUseCase findBookingUseCase;
    private final CreateBookingUseCase createBookingUseCase;
    private final CancelBookingUseCase cancelBookingUseCase;

    private final ActorContextHolder actorContextHolder;

    @Operation(summary = "예약 조회", description = "특정 ID의 예약을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @GetMapping("/{reservationId}")
    public ResponseEntity<BookingDetailResult> findBooking(
            @PathVariable("reservationId") UUID reservationId
    ) {
        var query = FindBookingQuery.of(reservationId);
        var result = findBookingUseCase.find(query);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "예약 생성", description = "특정 좌석의 시간 슬롯에 대해서 예약을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @PostMapping("/booking")
    public ResponseEntity<BookingResult> createBooking(
            @Valid @RequestBody CreateBookingRequest request
    ) {
        var actor = actorContextHolder.getActor();
        var command = request.toCommand(actor.subject());
        var result = createBookingUseCase.create(command);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "예약 취소", description = "생성된 예약을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "예약 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "권한 없음")
    })
    @DeleteMapping("/booking")
    public ResponseEntity<ReservationResult> cancelBooking(
            @Valid @RequestBody CancelBookingRequest request
    ) {
        var command = request.toCommand();
        var result = cancelBookingUseCase.cancel(command);
        return ResponseEntity.ok(result);
    }
}