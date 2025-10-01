package com.bread.popupbread.global.exception.common;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class InvalidUploadTypeException extends BaseException {
    public InvalidUploadTypeException() {
        super(ErrorCode.INVALID_UPLOAD_TYPE);
    }
}
