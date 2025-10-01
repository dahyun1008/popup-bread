package com.bread.popupbread.domain.popup;

import com.bread.popupbread.common.auth.TestAuthConfig;
import com.bread.popupbread.common.image.PresingedUrlProvider;
import com.bread.popupbread.domain.popup.dto.PopupCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestAuthConfig.class)
public class PopupIntegrationTest {
    @MockBean
    private PresingedUrlProvider presingedUrlProvider;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Supplier<String> testJwtProvider;

    @Test
    void 필터_없이_팝업_조회() throws Exception {
        // given
        int limit = 10;

        // when & then
        mockMvc.perform(get("/api/popups")
                .cookie(new Cookie("access-token",  testJwtProvider.get()))
                .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("팝업 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.paging.limit").value(limit))
                .andExpect(jsonPath("$.meta.paging.returnedCount", lessThanOrEqualTo(limit)));
    }

    @Test
    void region_district_필터_걸고_팝업_조회() throws Exception {
        // given
        int limit = 10;

        // when & then
        mockMvc.perform(get("/api/popups")
                        .cookie(new Cookie("access-token",  testJwtProvider.get()))
                        .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .param("limit", String.valueOf(limit))
                        .param("region", "서울")
                        .param("district", "강남"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("팝업 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.paging.limit").value(limit))
                .andExpect(jsonPath("$.meta.paging.returnedCount", lessThanOrEqualTo(limit)));
    }

    @Test
    void region_필터_걸고_팝업_조회() throws Exception {
        // given
        int limit = 10;

        // when & then
        // when & then
        mockMvc.perform(get("/api/popups")
                        .cookie(new Cookie("access-token",  testJwtProvider.get()))
                        .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .param("limit", String.valueOf(limit))
                        .param("region", "서울"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("팝업 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.meta.paging.limit").value(limit))
                .andExpect(jsonPath("$.meta.paging.returnedCount", lessThanOrEqualTo(limit)));
    }

    @Test
    void region_없이_district만_걸면_실패_응답() throws Exception {
        // given
        int limit = 10;

        // when & then
        // when & then
        mockMvc.perform(get("/api/popups")
                        .cookie(new Cookie("access-token",  testJwtProvider.get()))
                        .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .param("limit", String.valueOf(limit))
                        .param("district", "강남"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 제공하지_않는_region_district_걸면_404_실패_응답() throws Exception {
        // given
        int limit = 10;

        // when & then
        mockMvc.perform(get("/api/popups")
                        .cookie(new Cookie("access-token",  testJwtProvider.get()))
                        .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .param("limit", String.valueOf(limit))
                        .param("region", "개성")
                        .param("district", "강남"))
                .andExpect(status().isNotFound());
    }

    @Test
    void 조회_limit_크게_요청하면_실패_응답() throws Exception {
        // given
        int limit = 100;

        // when & then
        mockMvc.perform(get("/api/popups")
                        .cookie(new Cookie("access-token",  testJwtProvider.get()))
                        .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .param("limit", String.valueOf(limit))
                        .param("region", "서울")
                        .param("district", "강남")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void 팝업_생성_요청_처리후_성공응답과_Location헤더에_아이디반환() throws Exception {
        // given
        PopupCreateRequest req = PopupCreateRequest.builder()
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

        // when & then
        mockMvc.perform(post("/api/popups")
                        .cookie(new Cookie("access-token", testJwtProvider.get()))
                        .header(HttpHeaders.AUTHORIZATION, testJwtProvider.get())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, Matchers.matchesPattern("/popups/\\d+")));
    }

}
