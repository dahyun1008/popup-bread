package com.bread.popupbread.common.image.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadUrlBatchRequest {

    @Valid
    @NotEmpty(message = "Upload Url 요청에서 파일 목록은 비어있을 수 없습니다.")
    @Size(max = 5, message = "이미지는 최대 5개까지 등록할 수 있습니다.")
    private List<UploadUrlRequest> uploadUrlRequests;
}
