package com.bread.popupbread.global.exception.popup;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class InvalidPopupStatusException extends BaseException {
    public InvalidPopupStatusException() {
        super(ErrorCode.INVALID_POPUP_STATUS_VALUE);
    }
}
