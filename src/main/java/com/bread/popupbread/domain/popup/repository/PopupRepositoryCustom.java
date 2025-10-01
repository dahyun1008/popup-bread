package com.bread.popupbread.domain.popup.repository;

import com.bread.popupbread.domain.popup.Popup;
import com.bread.popupbread.domain.popup.dto.PopupListRequest;

import java.util.List;

public interface PopupRepositoryCustom {
    List<Popup> findByFilters(PopupListRequest req);
}
