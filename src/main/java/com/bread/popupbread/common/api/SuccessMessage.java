package com.bread.popupbread.common.api;

public enum SuccessMessage {
    PRESIGNED_URL_ISSUED("Presigned URL이 발급되었습니다."),
    POPUP_CREATED("팝업 등록에 성공했습니다."),
    COMMENT_DELETED("댓글 삭제에 성공했습니다.");

    private final String message;

    SuccessMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
