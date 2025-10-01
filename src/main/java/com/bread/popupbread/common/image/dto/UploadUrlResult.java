package com.bread.popupbread.common.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadUrlResult {
    private String fileName;
    private String uploadUrl;
    private String imageKey;
}
