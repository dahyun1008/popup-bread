package com.bread.popupbread.common.image;

import com.bread.popupbread.common.image.dto.DownloadUrlBatchRequest;
import com.bread.popupbread.common.image.dto.UploadUrlBatchRequest;
import com.bread.popupbread.common.image.dto.UploadUrlRequest;
import com.bread.popupbread.common.image.service.DownloadUrlService;
import com.bread.popupbread.common.image.service.ImageService;
import com.bread.popupbread.common.image.service.UploadUrlService;
import com.bread.popupbread.global.exception.advice.ApiExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageController.class)
@Import({ImageService.class, UploadUrlService.class, DownloadUrlService.class, ApiExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ImageIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private PresingedUrlProvider presingedUrlProvider;

    @Test
    void upload_url_배치_요청_성공시_200과_presigned_urls반환() throws Exception {
        // given
        String fileName1 = "a.jpg";
        String fileName2 = "b.png";
        String uploadType = "popup";

        UploadUrlBatchRequest request = UploadUrlBatchRequest.builder()
                .uploadUrlRequests(List.of(
                        UploadUrlRequest.builder().fileName(fileName1).contentType("image/jpeg").uploadType(uploadType).build(),
                        UploadUrlRequest.builder().fileName(fileName2).contentType("image/png").uploadType(uploadType).build()
                ))
                .build();

        given(presingedUrlProvider.generateUploadUrl(anyString(), eq("image/jpeg")))
                .willReturn("https://storage.googleapis.com/bucket/" + fileName1);
        given(presingedUrlProvider.generateUploadUrl(anyString(), eq("image/png")))
                .willReturn("https://storage.googleapis.com/bucket/" + fileName1);

        // when & then
        mockMvc.perform(post("/api/images/upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successes[0].uploadUrl").exists())
                .andExpect(jsonPath("$.data.successes[1].fileName").exists())
                .andExpect(jsonPath("$.data.successes[1].uploadUrl").exists())
                .andExpect(jsonPath("$.data.failures").isEmpty());
    }

    @Test
    void upload_url_배치_요청시_잘못된_파일명은_failures에_담김() throws Exception {
        // given
        String uploadType = "popup";

        UploadUrlBatchRequest request = UploadUrlBatchRequest.builder()
                .uploadUrlRequests(List.of(
                        UploadUrlRequest.builder().fileName("valid.jpg").contentType("image/jpeg").uploadType(uploadType).build(),
                        UploadUrlRequest.builder().fileName("invalidfile").contentType("image/jpeg").uploadType(uploadType).build()
                ))
                .build();

        given(presingedUrlProvider.generateUploadUrl(anyString(), eq("image/jpeg")))
                .willReturn("https://storage.googleapis.com/bucket/valid.jpg");

        // when & then
        mockMvc.perform(post("/api/images/upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successes[0].uploadUrl").exists())
                .andExpect(jsonPath("$.data.failures[0].code").value("INVALID_FILE_NAME"));
    }

    @Test
    void download_url_배치_요청_성공시_200과_presigned_urls반환() throws Exception {
        // given
        String imageKey = "popups/a.jpg";
        DownloadUrlBatchRequest req = DownloadUrlBatchRequest.builder()
                .downloadUrlRequests(List.of(imageKey))
                .build();
        given(presingedUrlProvider.generateDownloadUrl(imageKey))
                .willReturn("https://storage.googleapis.com/bucket/" + imageKey);

        // when & then
        mockMvc.perform(post("/api/images/download-url")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successes[0].downloadUrl").exists());

    }

    @Test
    void download_url_배치_요청시_잘못된_이미지_키는_failures에_담김() throws Exception {
        // given
        String imageKey = "popups/b.heic";
        DownloadUrlBatchRequest req = DownloadUrlBatchRequest.builder()
                .downloadUrlRequests(List.of(imageKey))
                .build();

        // when & then
        mockMvc.perform(post("/api/images/download-url")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.failures[0]").exists());
    }
}
