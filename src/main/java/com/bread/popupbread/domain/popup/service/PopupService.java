package com.bread.popupbread.domain.popup.service;

import com.bread.popupbread.common.api.Meta;
import com.bread.popupbread.common.paging.Cursor;
import com.bread.popupbread.domain.popup.Location;
import com.bread.popupbread.domain.popup.Popup;
import com.bread.popupbread.domain.popup.PopupImage;
import com.bread.popupbread.domain.popup.dto.*;
import com.bread.popupbread.domain.popup.repository.LocationRepository;
import com.bread.popupbread.domain.popup.repository.PopupRepository;
import com.bread.popupbread.global.exception.auth.UnauthorizedException;
import com.bread.popupbread.global.exception.common.InternalServerException;
import com.bread.popupbread.global.exception.common.ValidationException;
import com.bread.popupbread.global.exception.popup.InvalidLocationException;
import com.bread.popupbread.global.exception.popup.LocationNotFoundException;
import com.bread.popupbread.global.exception.popup.PopupNotFoundViewException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PopupService {
    private final PopupRepository popupRepository;
    private final LocationRepository locationRepository;

    @Transactional
    public PopupCreateResult create(PopupCreateRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException();
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        Long userId = Long.valueOf(userDetails.getUsername());

        String region = req.getLocation().getRegion();
        String district = req.getLocation().getDistrict();

        Location location = null;
        if ("온라인".equals(region)) {
            location = locationRepository.findByRegionAndDistrictIsNull(region)
                    .orElseThrow(() -> new ValidationException(Map.of("region", "온라인 region이 존재하지 않습니다.")));
        } else {
            location = locationRepository.findByRegionAndDistrict(region, district)
                    .orElseThrow(() -> new ValidationException(Map.of("region", "존재하지 않는 region/district 조합입니다.")));
        }

        Popup popup = Popup.builder()
                .title(req.getTitle())
                .userId(userId)
                .location(location)
                .detailLocation(req.getLocation().getDetailLocation())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .coverImageKey(req.getCoverImageKey())
                .build();

        if (req.getImageKeys() != null) {
            for (int i = 0; i < req.getImageKeys().size(); i++) {
                PopupImage popupImage = PopupImage.builder()
                        .imageKey(req.getImageKeys().get(i))
                        .sortOrder(i)
                        .status(PopupImage.Status.ACTIVE)
                        .build();
                popup.addImage(popupImage);
            }
        }

        try {
            Popup saved = popupRepository.save(popup);

            return PopupCreateResult.builder()
                    .popupId(saved.getPopupId())
                    .build();
        } catch (Exception ex) {
            throw new InternalServerException();
        }
    }

    public PopupListResult getPopups(PopupListRequest req) {
        if (req.getRegion() == null && req.getDistrict() != null) {
            throw new InvalidLocationException();
        }
        if (req.getRegion() != null) {
            boolean exists = (req.getDistrict() == null)
                    ? locationRepository.existsByRegion(req.getRegion())
                    : locationRepository.existsByRegionAndDistrict(req.getRegion(), req.getDistrict());
            if (!exists) {
                throw new LocationNotFoundException();
            }
        }

        List<Popup> popups = popupRepository.findByFilters(req);

        boolean hasNext = popups.size() > req.getLimit();
        if (hasNext) {
            popups = popups.subList(0, req.getLimit());
        }

        String nextCursor = hasNext ? Cursor.of(
                popups.get(popups.size() - 1).getStartDate(),
                popups.get(popups.size() - 1).getPopupId()
        ).toBase64() : null;

        List<PopupSummary> summaries = popups.stream()
                .map(p -> PopupSummary.builder()
                .popupId(p.getPopupId())
                .title(p.getTitle())
                .startDate(p.getStartDate().toString())
                .endDate(p.getEndDate().toString())
                .coverImageUrl(p.getCoverImageKey() != null ? p.getCoverImageKey() : "images/bread.jpg")
                .place(resolvePlace(p.getLocation().getId()))
                .detailLocation(p.getDetailLocation())
                .build())
                .toList();

        Meta.Paging paging = Meta.Paging.builder()
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .returnedCount(summaries.size())
                .limit(req.getLimit())
                .build();

        Meta meta = Meta.builder()
                .paging(paging)
                .build();

        return PopupListResult.builder()
                .data(summaries)
                .meta(meta)
                .build();
    }

    @Transactional(readOnly = true)
    public PopupDetail getPopupDetail(Long id) {
        Popup popup = popupRepository.findById(id)
                .orElseThrow(() -> new PopupNotFoundViewException());
        Location loc = locationRepository.findById(popup.getLocation().getId())
                .orElseThrow(() -> new LocationNotFoundException());
        List<String> imageKeys = popup.getPopupImages().stream()
                .filter(img -> img.getStatus() == PopupImage.Status.ACTIVE)
                .sorted(Comparator.comparing(PopupImage::getSortOrder))
                .map(PopupImage::getImageKey)
                .toList();

        return PopupDetail.builder()
                .popupId(popup.getPopupId())
                .title(popup.getTitle())
                .place(loc.getDistrict() == null
                        ? loc.getRegion()
                        : loc.getRegion()+" "+loc.getDistrict())
                .detailLocation(popup.getDetailLocation() == null ? "": popup.getDetailLocation())
                .startDate(popup.getStartDate())
                .endDate(popup.getEndDate())
                .coverImageKey(popup.getCoverImageKey())
                .imageKeys(imageKeys)
                .build();
    }

    private String resolvePlace(Integer locationId) {
        return locationRepository.findById(locationId)
                .map(loc -> loc.getRegion() + " " + (loc.getDistrict() != null ? loc.getDistrict() : " "))
                .orElse("알 수 없음");
    }
}
