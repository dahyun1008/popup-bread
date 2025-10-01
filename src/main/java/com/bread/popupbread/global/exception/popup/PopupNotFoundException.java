package com.bread.popupbread.global.exception.popup;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class PopupNotFoundException extends BaseException {
    public PopupNotFoundException() {
        super(ErrorCode.POPUP_NOT_FOUND);
    }
}
