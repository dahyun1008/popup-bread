package com.bread.popupbread.common.image;

import com.bread.popupbread.common.image.dto.*;
import com.bread.popupbread.common.image.service.DownloadUrlService;
import com.bread.popupbread.common.image.service.ImageService;
import com.bread.popupbread.common.image.service.UploadUrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ImageControllerTest {
    @MockBean
    private ImageService imageService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UploadUrlService uploadUrlService;
    @MockBean
    private DownloadUrlService downloadUrlService;

    @Test
    void presigned_url_발급_요청_성공시_presigned_url과_200_반환() throws Exception {
        // given
        String fileName = "uuid_filename.jpeg";
        String contentType = "image/jpeg";
        String uploadType = "popup";
        String presignedUrl = "presigned_url";
        String imageKey = uploadType + "s/" + fileName;

        // 요청 DTO (배치라서 리스트로 감쌈)
        UploadUrlBatchRequest uploadUrlBatchRequest = UploadUrlBatchRequest.builder()
                .uploadUrlRequests(List.of(
                        UploadUrlRequest.builder()
                                .fileName(fileName)
                                .contentType(contentType)
                                .uploadType(uploadType)
                                .build()
                ))
                .build();

        // 응답 DTO (successes에 한 건 넣어줌)
        UploadUrlBatchResult uploadUrlBatchResult = UploadUrlBatchResult.builder()
                .successes(List.of(
                        UploadUrlResult.builder()
                                .uploadUrl(presignedUrl)
                                .imageKey(imageKey)
                                .build()
                ))
                .failures(List.of()) // 실패 없음
                .build();

        when(imageService.generateUploadUrlList(any(UploadUrlBatchRequest.class)))
                .thenReturn(uploadUrlBatchResult);

        // when & then
        mockMvc.perform(post("/api/images/upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(uploadUrlBatchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successes[0].uploadUrl").value(presignedUrl))
                .andExpect(jsonPath("$.data.successes[0].imageKey").value(org.hamcrest.Matchers.startsWith(uploadType + "s/")))
                .andExpect(jsonPath("$.data.failures").isEmpty());
    }

    @Test
    void presigned_url_발급_요청시_리스트에_유효하지_않은_항목이_있으면_failures에_담겨서_200_반환() throws Exception {
        // given
        String fileName = "valid.jpg";
        String contentType = "image/jpeg";
        String uploadType = "popup";

        // 요청 JSON (하나는 정상, 하나는 잘못된 요청)
        String invalidJson = """
            {
                "uploadUrlRequests": [
                    { "fileName": "valid.jpg", "contentType": "image/jpeg", "uploadType": "popup" },
                    { "contentType": "image/jpeg" }
                ]
            }
            """;

        // 응답 Mock 세팅
        UploadUrlBatchResult uploadUrlBatchResult = UploadUrlBatchResult.builder()
                .successes(List.of(
                        UploadUrlResult.builder()
                                .uploadUrl("presigned_url")
                                .imageKey(uploadType + "/" + fileName)
                                .build()
                ))
                .failures(List.of(
                        UploadFailResult.builder()
                                .fileName(null) // 요청에 없었으므로 null
                                .code("VALIDATION_ERROR")
                                .message("파일명은 필수입니다.")
                                .build()
                ))
                .build();

        when(imageService.generateUploadUrlList(any(UploadUrlBatchRequest.class)))
                .thenReturn(uploadUrlBatchResult);

        // when & then
        mockMvc.perform(post("/api/images/upload-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successes[0].uploadUrl").value("presigned_url"))
                .andExpect(jsonPath("$.data.successes[0].imageKey").value(org.hamcrest.Matchers.startsWith(uploadType + "s/")))
                .andExpect(jsonPath("$.data.failures[0].code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.data.failures[0].message").value("파일명은 필수입니다."));
    }

    @Test
    void download_url_발급_후_200_반환() throws Exception {
        // given
        String imageKey = "popups/random-uuid_a.jpg";
        String downloadUrl = "presigned_url";
        DownloadUrlBatchRequest req = DownloadUrlBatchRequest.builder()
                .downloadUrlRequests(List.of(imageKey))
                .build();
        DownloadUrlResult result = DownloadUrlResult.builder()
                .imageKey(imageKey)
                .downloadUrl(downloadUrl)
                .build();
        when(imageService.generateDownloadUrlList(any(DownloadUrlBatchRequest.class)))
                .thenReturn(DownloadUrlBatchResult.builder()
                        .successes(List.of(result))
                        .build());

        // when & then
        mockMvc.perform(post("/api/images/download-url")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.successes[0].downloadUrl").value(downloadUrl));
    }

}
