package com.bread.popupbread.global.exception.auth;

public class MissingCodeException extends RuntimeException {
    public MissingCodeException(String message) {
        super(message);
    }
}
