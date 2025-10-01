package com.bread.popupbread.global.exception.comment;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

/**
 * 조회하려는 댓글이 존재하지 않을 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ApiExceptionHandler
 */
public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException() {
        super(ErrorCode.COMMENT_NOT_FOUND);
    }
}
