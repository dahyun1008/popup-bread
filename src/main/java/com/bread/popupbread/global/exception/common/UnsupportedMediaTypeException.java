package com.bread.popupbread.global.exception.common;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

/**
 * 지원하지 않는 미디어 타입에 대해 요청할 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ApiExceptionHandler
 */
public class UnsupportedMediaTypeException extends BaseException {
    public UnsupportedMediaTypeException() {
        super(ErrorCode.FILE_TYPE_NOT_SUPPORTED);
    }
}
