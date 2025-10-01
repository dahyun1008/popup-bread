package com.bread.popupbread.common.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UploadUrlBatchResult {
    private final List<UploadUrlResult> successes;
    private final List<UploadFailResult> failures;
}
