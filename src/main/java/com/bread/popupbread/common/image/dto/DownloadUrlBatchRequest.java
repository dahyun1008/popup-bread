package com.bread.popupbread.common.image.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadUrlBatchRequest {

    @Valid
    @NotEmpty(message = "Download Url 요청에서 파일 목록은 비어있을 수 없습니다.")
    @Size(max = 10, message = "Download Url 요청의 최대 크기는 10입니다.")
    private List<String> downloadUrlRequests;
}
