package com.bread.popupbread.domain.popup.dto;

// 팝업 상세 조회 API 응답용

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PopupDetail {
    private Long popupId;

    private String title;

    private String place;
    private String detailLocation;

    private LocalDate startDate;
    private LocalDate endDate;

    private String coverImageKey;

    private List<String> imageKeys;
}
