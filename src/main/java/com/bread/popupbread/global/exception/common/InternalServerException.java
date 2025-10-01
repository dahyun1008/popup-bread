package com.bread.popupbread.global.exception.common;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

/**
 * 서버 내부 처리 중 예기치 않은 오류가 일어났을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ApiExceptionHandler
 */
public class InternalServerException extends BaseException {
    public InternalServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
