package com.bread.popupbread.domain.popup.dto;

import com.bread.popupbread.domain.popup.dto.validation.PopupCreateRequestValid;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@PopupCreateRequestValid
public class PopupCreateRequest {

    @NotBlank(message = "팝업 이름은 필수 입력 필드입니다.")
    @Size(max = 100)
    private String title;

    @NotNull(message = "지역은 필수 입력 필드입니다.")
    @Valid
    private Location location;

    @NotNull(message = "시작일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "종료일은 필수입니다.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Valid
    private List<String> imageKeys;

    private String coverImageKey;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Location {

        @NotBlank(message = "지역 대분류는 필수 입력 필드입니다.")
        private String region;

        private String district; // region이 '온라인'일 경우 null 허용

        private String detailLocation; // 선택적 입력 필드
    }
}
