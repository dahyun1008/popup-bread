package com.bread.popupbread.global.exception.common;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;
import lombok.Getter;

import java.util.Map;

/**
 * 요청의 입력 필드가 유효하지 않을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ApiExceptionHandler
 */
@Getter
public class ValidationException extends BaseException {
    private final Map<String, String> fieldErrors;

    public ValidationException(Map<String, String> fieldErrors) {
        super(ErrorCode.VALIDATION_ERROR);
        this.fieldErrors = fieldErrors;
    }
}
