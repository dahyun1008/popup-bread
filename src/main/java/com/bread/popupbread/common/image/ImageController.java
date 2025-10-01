package com.bread.popupbread.common.image;

import com.bread.popupbread.common.api.ApiResponse;
import com.bread.popupbread.common.api.SuccessMessage;
import com.bread.popupbread.common.image.dto.DownloadUrlBatchRequest;
import com.bread.popupbread.common.image.dto.DownloadUrlBatchResult;
import com.bread.popupbread.common.image.dto.UploadUrlBatchRequest;
import com.bread.popupbread.common.image.dto.UploadUrlBatchResult;
import com.bread.popupbread.common.image.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload-url")
    public ResponseEntity<ApiResponse<UploadUrlBatchResult>> uploadUrl(
            @Valid @RequestBody UploadUrlBatchRequest req
    ) {
        UploadUrlBatchResult result = imageService.generateUploadUrlList(req);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.PRESIGNED_URL_ISSUED.getMessage(), result));
    }

    @PostMapping("/download-url")
    public ResponseEntity<ApiResponse<DownloadUrlBatchResult>> downloadUrl(
            @Valid @RequestBody DownloadUrlBatchRequest req
    ) {
        DownloadUrlBatchResult result = imageService.generateDownloadUrlList(req);
        return ResponseEntity.ok(ApiResponse.success(SuccessMessage.PRESIGNED_URL_ISSUED.getMessage(), result));
    }
}
