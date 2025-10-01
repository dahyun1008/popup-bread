package com.bread.popupbread.global.exception.auth;

/**
 * 사용자 인증 도중 내부 서비스 오류가 발생했을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ViewExceptionHandler
 */
public class AuthServiceException extends RuntimeException{
    public AuthServiceException(String message) {
        super(message);
    }

    public AuthServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
