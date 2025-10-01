package com.bread.popupbread.domain.popup;


import com.bread.popupbread.domain.popup.dto.PopupCreateRequest;
import com.bread.popupbread.domain.popup.dto.PopupCreateResult;
import com.bread.popupbread.domain.popup.repository.LocationRepository;
import com.bread.popupbread.domain.popup.repository.PopupRepository;
import com.bread.popupbread.domain.popup.service.PopupService;
import com.bread.popupbread.global.exception.common.InternalServerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PopupServiceTest {
    @InjectMocks
    PopupService popupService;
    @Mock
    PopupRepository popupRepository;
    @Mock
    LocationRepository locationRepository;

    @BeforeEach
    void setUpAuth() {
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("100")
                .password("")
                .roles("USER")
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();;
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    void cleanAuth() {
        SecurityContextHolder.clearContext();
    }

    PopupCreateRequest validRequest() {
        return PopupCreateRequest.builder()
                .title("팝업 이름")
                .location(PopupCreateRequest.Location.builder()
                        .region("서울")
                        .district("강남")
                        .detailLocation("신세계 강남점")
                        .build())
                .startDate(LocalDate.of(2025, 7, 16))
                .endDate(LocalDate.of(2025, 7, 17))
                .coverImageKey("popups/a.jpg")
                .imageKeys(List.of("popups/a.jpg", "popups/b.jpg", "popups/c.jpg"))
                .build();
    }


    Popup validPopup(PopupCreateRequest req, Location location) {
        return Popup.builder()
                .popupId(123L)
                .userId(100L)
                .location(location)
                .title(req.getTitle())
                .detailLocation("신세계 강남점")
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .coverImageKey(req.getCoverImageKey())
                .build();
    }
    @Test
    void 팝업_생성_성공시_PopupCreateResult_객체에_팝업_아이디_넣어서_반환() {
        // given
        PopupCreateRequest req = validRequest();
        Location location = Location.builder()
                .id(1)
                .region("서울")
                .district("강남")
                .build();

        Popup saved = validPopup(req, location);
        when(popupRepository.save(any(Popup.class))).thenReturn(saved);
        when(locationRepository.findByRegionAndDistrict(any(), any()))
                .thenReturn(Optional.of(location));

        // when
        PopupCreateResult result = popupService.create(req);

        // then
        assertThat(result.getPopupId()).isEqualTo(123L);
    }

    @Test
    void repository에서_예외_반환시_서비스도_InternalServerException_던지기(){
        // given
        PopupCreateRequest req = validRequest();
        Location location = Location.builder()
                .id(1)
                .region("서울")
                .district("강남")
                .build();

        when(popupRepository.save(any(Popup.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key exception"));
        when(locationRepository.findByRegionAndDistrict(any(), any()))
                .thenReturn(Optional.of(location));

        // when & then
        assertThatThrownBy(() -> popupService.create(req))
                .isInstanceOf(InternalServerException.class);
    }
}
