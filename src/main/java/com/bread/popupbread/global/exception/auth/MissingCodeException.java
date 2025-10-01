package com.bread.popupbread.global.exception.auth;

/**
 * OAuth2 인가 코드가 누락되었을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ViewExceptionHandler
 */
public class MissingCodeException extends RuntimeException {
    public MissingCodeException(String message) {
        super(message);
    }

    public MissingCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
