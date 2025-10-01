package com.bread.popupbread.domain.popup;

import com.bread.popupbread.domain.popup.controller.PopupController;
import com.bread.popupbread.domain.popup.dto.PopupCreateRequest;
import com.bread.popupbread.domain.popup.dto.PopupCreateResult;
import com.bread.popupbread.domain.popup.service.PopupService;
import com.bread.popupbread.global.exception.advice.ApiExceptionHandler;
import com.bread.popupbread.global.exception.advice.ViewExceptionHandler;
import com.bread.popupbread.global.exception.common.InternalServerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = PopupController.class)
@Import({ApiExceptionHandler.class, ViewExceptionHandler.class})
public class PopupControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean
    PopupService popupService;

    private Map<String, Object> validRequestBody() {
        return new LinkedHashMap<>(Map.of(
                "title", "팝업 이름",
                "location", Map.of(
                        "region", "서울",
                        "district", "강남",
                        "detailLocation", "신세계 강남점"
                ),
                "startDate", "2025-07-16",
                "endDate", "2025-07-17",
                "imageKeys", List.of("popups/uuid_banner.jpg", "popups/uuid_inside1.jpg"),
                "coverImageKey", "popups/uuid_banner.jpg"
        ));
    }

    private String validRequestJson() throws Exception {
        return objectMapper.writeValueAsString(validRequestBody());
    }

    @Nested
    @DisplayName("팝업 등록 API")
    public class CreatePopup {
        @Test
        void 팝업_등록_성공시_성공_응답_반환() throws Exception {
            // given
            PopupCreateResult result = PopupCreateResult.builder()
                    .popupId(123L)
                    .build();
            when(popupService.create(any())).thenReturn(result);

            // when & then
            mockMvc.perform(post("/api/popups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(validRequestJson())
                    )
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "http://localhost/popups/123"));
        }

        @Nested
        @DisplayName("팝업 등록 요청 검증 실패")
        class ValidationFail {

            @Test
            void 제목이_100자를_넘으면_400() throws Exception {
                Map<String, Object> body = validRequestBody();
                body.put("title", "A".repeat(101));

                mockMvc.perform(post("/api/popups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            void 종료일이_시작일보다_빠르면_400() throws Exception {
                Map<String, Object> body = validRequestBody();
                body.put("endDate", "2025-07-16");
                body.put("startDate", "2025-07-17");

                mockMvc.perform(post("/api/popups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            void 시작일이_2025년_이전이면_400() throws Exception {
                Map<String, Object> body = validRequestBody();
                body.put("startDate", "2024-07-16");

                mockMvc.perform(post("/api/popups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            void 커버사진이_사진리스트에_포함되지_않은_경우_400() throws Exception {
                Map<String, Object> body = validRequestBody();
                body.put("imageKeys", List.of("popups/uuid_inside1.jpg"));

                mockMvc.perform(post("/api/popups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isBadRequest());
            }

            @Test
            void 오프라인인데_district필드가_비어있는_경우_400() throws Exception {
                Map<String, Object> body = validRequestBody();
                PopupCreateRequest.Location location = PopupCreateRequest.Location.builder()
                        .region("서울")
                        .district("")
                        .build();
                body.put("location", location);

                mockMvc.perform(post("/api/popups")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                        .andExpect(status().isBadRequest());
            }
        }

//        @Test
//        void 인증되지_않은_사용자가_팝업_등록_요청시_401_실패_응답() {
//            // 요청 필터를 꺼둬서 빠로 분리 필요
//        }

        @Test
        void 서버_내부_오류_발생시_500_실패_응답() throws Exception {
            // given
            when(popupService.create(any())).thenThrow(new InternalServerException());

            // when & then
            mockMvc.perform(post("/api/popups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validRequestJson())
                    )
                    .andExpect(status().isInternalServerError());
        }
    }
}
