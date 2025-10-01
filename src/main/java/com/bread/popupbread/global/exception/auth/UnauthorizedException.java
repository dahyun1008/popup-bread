package com.bread.popupbread.global.exception.auth;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

/**
 * 인증되지 않은 사용자가 요청을 보냈을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ApiExceptionHandler
 */
public class UnauthorizedException extends BaseException {
    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}