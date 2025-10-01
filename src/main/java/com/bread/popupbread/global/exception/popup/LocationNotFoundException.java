package com.bread.popupbread.global.exception.popup;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

public class LocationNotFoundException extends BaseException {
    public LocationNotFoundException() {
        super(ErrorCode.LOCATION_NOT_FOUND);
    }
}
