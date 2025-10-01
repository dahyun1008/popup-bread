package com.bread.popupbread.global.exception.advice;

import com.bread.popupbread.common.api.ApiResponse;
import com.bread.popupbread.common.api.ErrorBody;
import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;
import com.bread.popupbread.global.exception.auth.UnauthorizedException;
import com.bread.popupbread.global.exception.comment.CommentForbiddenException;
import com.bread.popupbread.global.exception.comment.CommentNotFoundException;
import com.bread.popupbread.global.exception.common.*;
import com.bread.popupbread.global.exception.popup.*;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(BaseException ex) {
        ErrorBody errorBody = ErrorBody.builder()
                .code(ex.getErrorCode().getCode())
                .build();

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.fail(ex.getErrorCode().getDefaultMessage(), errorBody));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        Map<String, String> fieldErrors = new HashMap<>();

        if (cause instanceof InvalidFormatException invalidFormatException) {
            List<JsonMappingException.Reference> path = invalidFormatException.getPath();
            if (!path.isEmpty()) {
                String fieldName = path.get(0).getFieldName();
                fieldErrors.put(fieldName, "형식이 올바르지 않습니다.");
            }
        }

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ErrorBody errorBody = ErrorBody.builder()
                .code(errorCode.getCode())
                .fieldErrors(fieldErrors.isEmpty() ? null : fieldErrors)
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getDefaultMessage(), errorBody));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorBody errorBody = ErrorBody.builder()
                .code(errorCode.getCode())
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getDefaultMessage(), errorBody));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(ValidationException ex) {
        if (ex.getFieldErrors() != null && !ex.getFieldErrors().isEmpty()) {
            ex.getFieldErrors().forEach((field, message) ->
                    log.warn("Validation failed: field={}, message={}", field, message)
            );
        } else {
            log.warn("Validation failed: {}", ex.getMessage());
        }

        ErrorBody errorBody = ErrorBody.builder()
                .code(ex.getErrorCode().getCode())
                .fieldErrors(ex.getFieldErrors())
                .build();

        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(ApiResponse.fail(ex.getErrorCode().getDefaultMessage(), errorBody));
    }

    @ExceptionHandler({
            UnauthorizedException.class,
            PopupForbiddenException.class,
            PopupNotFoundException.class,
            CommentForbiddenException.class,
            CommentNotFoundException.class,
            UnsupportedMediaTypeException.class,
            InternalServerException.class,
            InvalidFileNameException.class,
            InvalidUploadTypeException.class,
            InvalidPopupStatusException.class,
            InvalidLocationException.class,
            LocationNotFoundException.class,
    })
    public ResponseEntity<ApiResponse<?>> handleException(BaseException ex) {
        return buildErrorResponse(ex);
    }
}
