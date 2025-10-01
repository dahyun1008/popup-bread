package com.bread.popupbread.common.image;

import com.bread.popupbread.common.image.dto.*;
import com.bread.popupbread.common.image.service.DownloadUrlService;
import com.bread.popupbread.common.image.service.ImageService;
import com.bread.popupbread.common.image.service.UploadUrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageServiceTest {
    private PresingedUrlProvider presingedUrlProvider;
    private UploadUrlService uploadUrlService;
    private DownloadUrlService downloadUrlService;
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        presingedUrlProvider = mock(PresingedUrlProvider.class);
        uploadUrlService = new UploadUrlService(presingedUrlProvider);
        downloadUrlService = new DownloadUrlService(presingedUrlProvider);
        imageService = new ImageService(uploadUrlService, downloadUrlService);
    }

    @Test
    void upload_url_배치_요청_모두_성공시_successes에_담긴다() {
        // given
        String fileName1 = "a.jpg";
        String fileName2 = "b.png";
        String contentType = "image/jpeg";
        String uploadType = "popup";

        UploadUrlBatchRequest batchRequest = UploadUrlBatchRequest.builder()
                .uploadUrlRequests(List.of(
                        UploadUrlRequest.builder().fileName(fileName1).contentType(contentType).uploadType(uploadType).build(),
                        UploadUrlRequest.builder().fileName(fileName2).contentType(contentType).uploadType(uploadType).build()
                ))
                .build();

        when(presingedUrlProvider.generateUploadUrl(anyString(), anyString()))
                .thenReturn("https://storage.googleapis.com/url1", "https://storage.googleapis.com/url2");

        // when
        UploadUrlBatchResult result = imageService.generateUploadUrlList(batchRequest);

        // then
        assertThat(result.getSuccesses()).hasSize(2);
        assertThat(result.getFailures()).isEmpty();
    }


    @Test
    void upload_url_배치_요청_중_일부가_실패하면_failures에_담긴다() {
        // given
        String validFile = "valid.jpg";
        String invalidFile = "invalidfile"; // 확장자 없음
        String contentType = "image/jpeg";
        String uploadType = "popup";

        UploadUrlBatchRequest batchRequest = UploadUrlBatchRequest.builder()
                .uploadUrlRequests(List.of(
                        UploadUrlRequest.builder().fileName(validFile).contentType(contentType).uploadType(uploadType).build(),
                        UploadUrlRequest.builder().fileName(invalidFile).contentType(contentType).uploadType(uploadType).build()
                ))
                .build();

        when(presingedUrlProvider.generateUploadUrl(anyString(), anyString()))
                .thenReturn("https://storage.googleapis.com/url");

        // when
        UploadUrlBatchResult result = imageService.generateUploadUrlList(batchRequest);

        // then
        assertThat(result.getSuccesses()).hasSize(1);
        assertThat(result.getFailures()).hasSize(1);
    }


    @Test
    void upload_url_배치_요청_모두_실패하면_failures만_존재한다() {
        // given
        UploadUrlBatchRequest batchRequest = UploadUrlBatchRequest.builder()
                .uploadUrlRequests(List.of(
                        UploadUrlRequest.builder().fileName("badfile").contentType("image/jpeg").uploadType("popup").build(),
                        UploadUrlRequest.builder().fileName("anotherbad").contentType("image/jpeg").uploadType("popup").build()
                ))
                .build();

        // when
        UploadUrlBatchResult result = imageService.generateUploadUrlList(batchRequest);

        // then
        assertThat(result.getSuccesses()).isEmpty();
        assertThat(result.getFailures()).hasSize(2);
    }

    @Test
    void download_url_베치_요청_모두_성공시_successes에_담긴다() {
        // given
        String imageKey1 = "popups/imageKey1.jpg";
        String imageKey2 = "popups/imageKey2.jpg";
        String downloadUrl = "https://storage.googleapis.com/url";
        DownloadUrlBatchRequest batchRequest = DownloadUrlBatchRequest.builder()
                .downloadUrlRequests(List.of(imageKey1, imageKey2))
                .build();
        when(presingedUrlProvider.generateDownloadUrl(anyString()))
                .thenReturn(downloadUrl);

        // when
        DownloadUrlBatchResult result = imageService.generateDownloadUrlList(batchRequest);

        // then
        assertThat(result.getSuccesses()).hasSize(2);
        assertThat(result.getFailures()).isEmpty();
    }

    @Test
    void download_url_배치_요청_중_일부가_실패하면_failures에_담긴다() {
        // given
        String imageKey1 = "popups/imageKey1.jpg";
        String imageKey2 = "pop/imageKey2.jpg";
        String downloadUrl = "https://storage.googleapis.com/url";
        DownloadUrlBatchRequest batchRequest = DownloadUrlBatchRequest.builder()
                .downloadUrlRequests(List.of(imageKey1, imageKey2))
                .build();
        when(presingedUrlProvider.generateDownloadUrl(anyString()))
                .thenReturn(downloadUrl);

        // when
        DownloadUrlBatchResult result = imageService.generateDownloadUrlList(batchRequest);

        // then
        assertThat(result.getSuccesses()).hasSize(1);
        assertThat(result.getFailures()).hasSize(1);
    }

    @Test
    void download_url_배치_요청_모두_실패하면_failures만_존재한다() {
        // given
        String imageKey1 = "popup/imageKey1.jpg";
        String imageKey2 = "pop/imageKey2.jpg";
        String downloadUrl = "https://storage.googleapis.com/url";
        DownloadUrlBatchRequest batchRequest = DownloadUrlBatchRequest.builder()
                .downloadUrlRequests(List.of(imageKey1, imageKey2))
                .build();
        when(presingedUrlProvider.generateDownloadUrl(anyString()))
                .thenReturn(downloadUrl);

        // when
        DownloadUrlBatchResult result = imageService.generateDownloadUrlList(batchRequest);

        // then
        assertThat(result.getSuccesses()).isEmpty();
        assertThat(result.getFailures()).hasSize(2);
    }
}
