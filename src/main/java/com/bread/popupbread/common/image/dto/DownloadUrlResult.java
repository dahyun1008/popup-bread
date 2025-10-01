package com.bread.popupbread.common.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadUrlResult {
    private String imageKey;
    private String downloadUrl;
}
