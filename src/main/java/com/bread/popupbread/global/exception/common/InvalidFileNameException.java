package com.bread.popupbread.global.exception.common;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class InvalidFileNameException extends BaseException {
    public InvalidFileNameException() {
        super(ErrorCode.INVALID_FILE_NAME);
    }
}
