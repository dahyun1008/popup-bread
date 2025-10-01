package com.bread.popupbread.domain.popup.dto;

import lombok.*;

// 팝업 리스트 카드 뷰 용도 (간단 요약 정보 DTO)
// (1L, "팝업스토어 A", "서울 강남", "2025-08-01", "2025-08-10", "img/popup1.jpg"),
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PopupSummary {
    private Long popupId;
    private String title;
    private String place;
    private String detailLocation;
    private String startDate;
    private String endDate;
    private String coverImageUrl;
}
