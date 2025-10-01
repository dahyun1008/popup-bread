package com.bread.popupbread.domain.popup;

import com.bread.popupbread.global.exception.popup.InvalidPopupStatusException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PopupStatus {
    UPCOMING,
    ONGOING,
    ENDED;

    @JsonCreator
    public static PopupStatus fromString(String value) {
        try {
            return PopupStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidPopupStatusException();
        }
    }

    @JsonValue
    public String toValue() {
        return this.name().toLowerCase();
    }
}
