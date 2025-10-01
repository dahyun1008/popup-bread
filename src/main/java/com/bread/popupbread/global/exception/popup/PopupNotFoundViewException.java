package com.bread.popupbread.global.exception.popup;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class PopupNotFoundViewException extends BaseException {
    public PopupNotFoundViewException() {
        super(ErrorCode.POPUP_NOT_FOUND);
    }
}
