package com.bread.popupbread.domain.popup.repository;

import com.bread.popupbread.common.paging.Cursor;
import com.bread.popupbread.domain.popup.Popup;
import com.bread.popupbread.domain.popup.QLocation;
import com.bread.popupbread.domain.popup.QPopup;
import com.bread.popupbread.domain.popup.dto.PopupListRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class PopupRepositoryImpl implements PopupRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Popup> findByFilters(PopupListRequest req) {
        QPopup popup = QPopup.popup;
        QLocation location = QLocation.location;
        BooleanBuilder builder = new BooleanBuilder();

        LocalDate now = LocalDate.now();
        if (req.getStatus() != null) {
            switch (req.getStatus()) {
                case UPCOMING -> builder.and(popup.startDate.after(now));
                case ONGOING -> builder.and(popup.startDate.loe(now).and(popup.endDate.goe(now)));
                case ENDED -> builder.and(popup.endDate.loe(now));
            }
        }
        if (req.getRegion() != null) {
            builder.and(location.region.eq(req.getRegion()));
        }
        if (req.getDistrict() != null) {
            builder.and(location.district.eq(req.getDistrict()));
        }

        if (req.getCursor() != null) {
            Cursor cursor = Cursor.fromBase64(req.getCursor());
            builder.and(
                    popup.startDate.lt(cursor.getStartDate())
                            .or(popup.startDate.eq(cursor.getStartDate())
                                    .and(popup.popupId.lt(cursor.getPopupId())))
            );
        }

        return jpaQueryFactory
                .selectFrom(popup)
                .join(popup.location, location).fetchJoin()
                .where(builder)
                .orderBy(popup.startDate.desc(), popup.popupId.desc())
                .limit(req.getLimit()+1)
                .fetch();
    }
}
