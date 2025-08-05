package com.bread.popupbread.global.exception;

import com.bread.popupbread.global.exception.auth.AuthServiceException;
import com.bread.popupbread.global.exception.auth.IllegalCodeException;
import com.bread.popupbread.global.exception.auth.MissingCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;

@ControllerAdvice
public class GlobalExceptionHandler {
    private ResponseEntity<Void> redirectToLoginWithError(String errorCode){
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/login?error=" + errorCode))
                .build();
    }

    @ExceptionHandler(MissingCodeException.class)
    public ResponseEntity<Void> handleMissingCodeException() {
        return redirectToLoginWithError("missing_code");
    }

    @ExceptionHandler(IllegalCodeException.class)
    public ResponseEntity<Void> handleIllegalCodeException() {
        return redirectToLoginWithError("illegal_code");
    }

    @ExceptionHandler(AuthServiceException.class)
    public ResponseEntity<Void> handleAuthServiceException() {
        return redirectToLoginWithError("auth_service");
    }
}
