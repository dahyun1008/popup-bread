package com.bread.popupbread.domain.popup.dto;


import com.bread.popupbread.domain.popup.PopupStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupListRequest {
    String region;
    String district;

    PopupStatus status;

    @NotNull(message = "limit 값은 필수입니다.")
    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    @Max(value = 20, message = "limit은 20 이하여야 합니다.")
    Integer limit;

    String cursor;
}
