package com.bread.popupbread.common.image.service;

import com.bread.popupbread.common.image.PresingedUrlProvider;
import com.bread.popupbread.common.image.UploadType;
import com.bread.popupbread.common.image.dto.*;
import com.bread.popupbread.global.exception.BaseException;
import com.bread.popupbread.global.exception.common.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadUrlService {
    private final PresingedUrlProvider presingedUrlProvider;
    private static final Set<String> ALLOWED_EXTENSION = Set.of("jpg", "jpeg", "png", "webp");

    public UploadUrlResult generateUploadUrl(UploadUrlRequest req){
        final UploadType uploadType;
        try {
            uploadType = UploadType.from(req.getUploadType());
        } catch (IllegalArgumentException e) {
            throw new InvalidUploadTypeException();
        }

        String fileName = req.getFileName();
        if (!fileName.contains(".")) {
            throw new InvalidFileNameException();
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSION.contains(extension)) {
            throw new UnsupportedMediaTypeException();
        }

        String key = uploadType.getPrefix() + "/" + UUID.randomUUID() + "_" + fileName;
        try {
            String presignedUrl = presingedUrlProvider.generateUploadUrl(key, req.getContentType());
            if (presignedUrl == null || presignedUrl.isBlank()) {
                throw new InternalServerException();
            }
            return UploadUrlResult.builder()
                    .fileName(fileName)
                    .uploadUrl(presignedUrl)
                    .imageKey(key)
                    .build();
        } catch (Exception ex) {
            throw new InternalServerException();
        }
    }

    public void validateRequest(UploadUrlRequest req) {
        if (req.getFileName() == null || req.getFileName().isBlank() ||
                req.getContentType() == null || req.getContentType().isBlank() ||
                req.getUploadType() == null || req.getUploadType().isBlank()) {
            throw new RequestValidationException();
        }
    }

    public UploadUrlBatchResult generateUploadUrlBatch(UploadUrlBatchRequest req) {
        List<UploadUrlResult> successes = new ArrayList<>();
        List<UploadFailResult> failures = new ArrayList<>();

        for (UploadUrlRequest fileReq: req.getUploadUrlRequests()) {
            try {
                validateRequest(fileReq);
                successes.add(generateUploadUrl(fileReq));
            } catch (BaseException ex) {
                failures.add(
                        UploadFailResult.builder()
                                .fileName(fileReq.getFileName())
                                .code(ex.getErrorCode().getCode())
                                .message(ex.getMessage())
                                .build()
                );
            }
        }

        return UploadUrlBatchResult.builder()
                .successes(successes)
                .failures(failures)
                .build();
    }
}
