package com.seatliberator.seatliberator.reservation.infrastructure.web.controller;

import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationErrorCode;
import com.seatliberator.seatliberator.vacancy.application.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = VacancyAlertNotificationController.class)
public class VacancyAlertControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, String>> handleApplicationException(ApplicationException exception) {
        var status = exception.getErrorCode() == ApplicationErrorCode.NOTIFICATION_ACCESS_DENIED
                ? HttpStatus.FORBIDDEN
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(
                Map.of(
                        "code", exception.getErrorCode().getCode(),
                        "message", exception.getMessage()
                )
        );
    }
}
