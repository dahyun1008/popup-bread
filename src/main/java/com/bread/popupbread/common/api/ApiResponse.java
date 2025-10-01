package com.bread.popupbread.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String message;
    private T data;
    private Meta meta;
    private ErrorBody error;

    /** 성공(메시지+데이터) */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().data(data).message(message).build();
    }

    /** 성공(메시지) */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder().data(null).message(message).build();
    }

    /** 메타 포함 성공 */
    public static <T> ApiResponse<T> successWithMeta(String message, T data, Meta meta) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .meta(meta)
                .build();
    }

    /** 실패(데이터+에러) */
    public static ApiResponse<Void> fail(String message, ErrorBody error) {
        return ApiResponse.<Void>builder()
                .message(message)
                .error(error)
                .build();
    }
}
