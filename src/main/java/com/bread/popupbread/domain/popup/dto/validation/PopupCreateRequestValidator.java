package com.bread.popupbread.domain.popup.dto.validation;

import com.bread.popupbread.domain.popup.dto.PopupCreateRequest;
import com.bread.popupbread.domain.popup.dto.PopupCreateResult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.List;

public class PopupCreateRequestValidator
        implements ConstraintValidator<PopupCreateRequestValid, PopupCreateRequest> {

    private static final LocalDate SERVICE_LAUNCH_DATE = LocalDate.of(2025, 1, 1);

    @Override
    public boolean isValid(PopupCreateRequest req, ConstraintValidatorContext ctx) {
        if (req == null) {return true;}

        boolean valid = true;
        ctx.disableDefaultConstraintViolation();

        if (req.getStartDate() != null && req.getEndDate() != null) {
            if (req.getEndDate().isBefore(req.getStartDate())) {
                add(ctx, "endDate", "종료일은 시작일보다 빠를 수 없습니다.");
                valid = false;
            }
        }

        if (req.getStartDate() != null && req.getStartDate().isBefore(SERVICE_LAUNCH_DATE)) {
            add(ctx, "startDate", "시작일은 2025-01-01 이후여야 합니다.");
            valid = false;
        }

        if (req.getCoverImageKey() != null) {
            List<String> keys = req.getImageKeys();
            if (keys == null || keys.isEmpty() || !keys.contains(req.getCoverImageKey())) {
                add(ctx, "coverImageKey", "coverImageKey는 imageKeys 목록에 포함되어야 합니다.");
                valid = false;
            }
        }

//        if (req.getLocation() != null && req.getLocation().getRegion() != null) {
//            String region = req.getLocation().getRegion();
//            String district = req.getLocation().getDistrict();
//            if (!"온라인".equals(region)) {
//                if (district == null || district.isBlank()) {
//                    add(ctx, "location.district", "오프라인 지역에서는 district가 필수입니다.");
//                    valid = false;
//                }
//            } else {
//                if (district != null && !district.isBlank()) {
//                    add(ctx, "location.region", "온라인 지역에서는 district가 있을 수 없습니다.");
//                    valid = false;
//                }
//            }
//        }

        return valid;
    }

    private void add(ConstraintValidatorContext ctx, String field, String message) {
        ctx.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
    }
}
