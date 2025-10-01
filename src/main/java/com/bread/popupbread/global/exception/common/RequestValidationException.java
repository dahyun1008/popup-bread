package com.bread.popupbread.global.exception.common;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class RequestValidationException extends BaseException {
    public RequestValidationException() {
        super(ErrorCode.VALIDATION_ERROR);
    }
}
