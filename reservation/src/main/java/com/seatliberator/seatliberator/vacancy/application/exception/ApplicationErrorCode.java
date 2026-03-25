package com.seatliberator.seatliberator.vacancy.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {
    DUPLICATED_REQUEST("RVC001", "동일한 알람을 중복해서 등록할 수 없습니다."),
    NOT_FOUND("RVC002","대상을 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
