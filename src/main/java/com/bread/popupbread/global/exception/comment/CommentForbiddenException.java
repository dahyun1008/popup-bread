package com.bread.popupbread.global.exception.comment;

import com.bread.popupbread.common.api.ErrorCode;
import com.bread.popupbread.global.exception.BaseException;

/**
 * 댓글 작성자 이외의 유저가 댓글을 삭제하려 할 때 발생하는 예외입니다.
 * @see com.bread.popupbread.global.exception.advice.ApiExceptionHandler
 */
public class CommentForbiddenException extends BaseException {
    public CommentForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
