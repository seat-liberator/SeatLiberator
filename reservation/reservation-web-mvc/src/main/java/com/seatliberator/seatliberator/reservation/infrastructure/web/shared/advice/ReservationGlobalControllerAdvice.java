package com.seatliberator.seatliberator.reservation.infrastructure.web.shared.advice;

import com.seatliberator.seatliberator.reservation.application.shared.exception.ReservationApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

@Slf4j
@RestControllerAdvice(basePackages = "com.seatliberator.seatliberator.reservation")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ReservationGlobalControllerAdvice {

    @ExceptionHandler(ReservationApplicationException.class)
    public ProblemDetail handleReservationApplicationException(
            ReservationApplicationException exception,
            HttpServletRequest request
    ) {
        var errorCode = exception.getErrorCode();
        var status = resolveStatus(errorCode);

        log.warn("Reservation application error. code={}, path={}", errorCode, request.getRequestURI());

        var problem = ProblemDetail.forStatusAndDetail(status, errorCode.getMessage());
        problem.setTitle(errorCode.name());
        problem.setType(URI.create("https://seatliberator/errors/" + errorCode.name().toLowerCase()));
        problem.setProperty("code", errorCode.name());
        return problem;
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ProblemDetail handleBadRequest(Exception exception, HttpServletRequest request) {
        log.warn("Bad request. type={}, path={}, message={}",
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getMessage());

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        problem.setTitle("BAD_REQUEST");
        problem.setType(URI.create("https://seatliberator/errors/bad-request"));
        problem.setProperty("code", "BAD_REQUEST");
        return problem;
    }

    @ExceptionHandler({
            AccessDeniedException.class,
            AuthorizationDeniedException.class
    })
    public ProblemDetail handelAccessDenied(Exception exception, HttpServletRequest request) {
        log.warn("AccessDenied type={}, path={}, message={}",
                exception.getClass().getSimpleName(),
                request.getRequestURI(),
                exception.getMessage());

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "권한이 없습니다");
        problem.setTitle("FORBIDDEN");
        problem.setType(URI.create("https://seatliberator/errors/forbidden"));
        problem.setProperty("code", "FORBIDDEN");
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        log.warn("Illegal argument. path={}, message={}", request.getRequestURI(), exception.getMessage());

        var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다.");
        problem.setTitle("INVALID_ARGUMENT");
        problem.setType(URI.create("https://seatliberator/errors/invalid-argument"));
        problem.setProperty("code", "INVALID_ARGUMENT");
        return problem;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        log.error("Illegal state. path={}", request.getRequestURI(), exception);

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "요청 처리 중 오류가 발생했습니다."
        );
        problem.setTitle("INTERNAL_SERVER_ERROR");
        problem.setType(URI.create("https://seatliberator/errors/internal-server-error"));
        problem.setProperty("code", "INTERNAL_SERVER_ERROR");
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnhandled(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Unhandled exception. path={}", request.getRequestURI(), exception);

        var problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "알 수 없는 오류가 발생했습니다."
        );
        problem.setTitle("UNEXPECTED_ERROR");
        problem.setType(URI.create("https://seatliberator/errors/unexpected-error"));
        problem.setProperty("code", "UNEXPECTED_ERROR");
        return problem;
    }

    private HttpStatus resolveStatus(Enum<?> errorCode) {
        return switch (errorCode.name()) {
            case "RESERVATION_NOT_FOUND", "SEAT_NOT_FOUND", "ROOM_NOT_FOUND" -> HttpStatus.NOT_FOUND;
            case "RESERVATION_ALREADY_EXISTS", "RESERVATION_TIME_CONFLICT" -> HttpStatus.CONFLICT;
            case "RESERVATION_READ_FORBIDDEN", "RESERVATION_VERIFY_FORBIDDEN" -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
