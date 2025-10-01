package com.bread.popupbread.global.exception.popup;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class PopupForbiddenException extends BaseException {
    public PopupForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
