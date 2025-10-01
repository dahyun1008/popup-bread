package com.bread.popupbread.common.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadFailResult {
    private String imageKey;
    private String code;
    private String message;
}
