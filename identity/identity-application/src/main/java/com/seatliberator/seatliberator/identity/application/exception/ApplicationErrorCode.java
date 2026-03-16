package com.seatliberator.seatliberator.identity.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApplicationErrorCode {
    AUTHENTICATION_FAILED("I001", "인증 실패"),
    EMAIL_DUPLICATED("I002", "이메일이 이미 존재합니다.");

    private final String code;
    private final String message;
}
