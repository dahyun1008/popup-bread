package com.bread.popupbread.common.paging;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;

@Getter
public class Cursor {
    private final LocalDate startDate;
    private final Long popupId;

    private Cursor(LocalDate startDate, Long popupId) {
        this.startDate = startDate;
        this.popupId = popupId;
    }

    public static Cursor of(LocalDate startDate, Long popupId) {
        return new Cursor(startDate, popupId);
    }

    public String toBase64() {
        String raw = startDate.toString() + "|" + popupId;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static Cursor fromBase64(String encoded) {
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
        String[] split = decoded.split("\\|");
        return new Cursor(LocalDate.parse(split[0]), Long.parseLong(split[1]));
    }
}
