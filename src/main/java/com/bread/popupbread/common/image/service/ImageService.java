package com.bread.popupbread.common.image.service;

import com.bread.popupbread.common.image.dto.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final UploadUrlService uploadUrlService;
    private final DownloadUrlService downloadUrlService;

    public UploadUrlBatchResult generateUploadUrlList(UploadUrlBatchRequest req) {
        return uploadUrlService.generateUploadUrlBatch(req);
    }

    public DownloadUrlBatchResult generateDownloadUrlList(DownloadUrlBatchRequest req) {
        return downloadUrlService.generateDownloadUrlBatch(req);
    }
}
