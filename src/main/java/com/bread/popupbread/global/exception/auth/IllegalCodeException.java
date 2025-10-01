package com.bread.popupbread.global.exception.auth;

/**
 * OAuth2 인가 코드가 유효하지 않을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ViewExceptionHandler
 */
public class IllegalCodeException extends IllegalArgumentException {
    public IllegalCodeException(String message) {
        super(message);
    }

    public IllegalCodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
