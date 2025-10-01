package com.bread.popupbread.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "권한이 없습니다."),
    POPUP_NOT_FOUND(404, "POPUP_NOT_FOUND", "해당 ID의 팝업 게시글을 찾을 수 없습니다."),
    LOCATION_NOT_FOUND(404, "LOCATION_NOT_FOUND", "해당 장소를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "COMMENT_NOT_FOUND", "해당 댓글을 찾을 수 없습니다."),
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "필수 입력값이 유효하지 않습니다."),
    INVALID_FILE_NAME(400, "INVALID_FILE_NAME", "파일명이 올바르지 않습니다. 확장자를 포함해주세요."),
    INVALID_UPLOAD_TYPE(400, "INVALID_UPLOAD_TYPE", "uploadType이 올바르지 않습니다."),
    INVALID_POPUP_STATUS_VALUE(400, "INVALID_POPUP_STATUS_VALUE", "허용되지 않은 팝업 상태 값입니다."),
    INVALID_LOCATION(400, "INVALID_LOCATION", "지원하지 않는 장소 형식입니다."),
    FILE_TYPE_NOT_SUPPORTED(415, "FILE_TYPE_NOT_SUPPORTED", "지원하지 않는 파일 타입입니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");

    private final int httpStatus;
    private final String code;
    private final String defaultMessage;
}
