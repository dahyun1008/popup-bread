package com.bread.popupbread.global.exception.popup;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class InvalidLocationException extends BaseException {
    public InvalidLocationException() {
      super(ErrorCode.INVALID_LOCATION);
    }
}
