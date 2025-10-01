package com.bread.popupbread.common.image;

import java.util.Arrays;

public enum UploadType {
    POPUP("popups"),
    CORRECTION("corrections"),
    PROFILE("profiles");

    private final String prefix;

    UploadType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public static UploadType from(String type) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 업로드 타입입니다: " + type)
                );
    }
}
