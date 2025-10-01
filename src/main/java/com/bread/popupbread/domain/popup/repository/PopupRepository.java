package com.bread.popupbread.domain.popup.repository;

import com.bread.popupbread.domain.popup.Popup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopupRepository extends JpaRepository<Popup, Long>, PopupRepositoryCustom {}
