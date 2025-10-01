package com.bread.popupbread.domain.popup.dto;

import com.bread.popupbread.common.api.Meta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopupListResult {
    private List<PopupSummary> data;
    private Meta meta;
}
