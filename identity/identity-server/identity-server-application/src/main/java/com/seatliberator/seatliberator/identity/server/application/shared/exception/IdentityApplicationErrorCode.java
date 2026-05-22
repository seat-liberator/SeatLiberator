package com.seatliberator.seatliberator.identity.server.application.shared.exception;

import com.seatliberator.seatliberator.kernel.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IdentityApplicationErrorCode implements ErrorCode {
    AUTHENTICATION_FAILED("I001", "인증 실패"),
    EMAIL_DUPLICATED("I002", "이메일이 이미 존재합니다."),
    USER_NOT_FOUND("I003", "사용자를 찾을 수 없습니다."),
    ACCOUNT_ALREADY_EXISTS("I004", "이미 존재하는 계정입니다."),
    ACCOUNT_NOT_FOUND("I005", "계정을 찾을 수 없습니다.");

    private final String code;
    private final String message;
}
