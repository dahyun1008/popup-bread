package com.bread.popupbread.common.image.service;

import com.bread.popupbread.common.image.PresingedUrlProvider;
import com.bread.popupbread.common.image.dto.DownloadFailResult;
import com.bread.popupbread.common.image.dto.DownloadUrlBatchRequest;
import com.bread.popupbread.common.image.dto.DownloadUrlBatchResult;
import com.bread.popupbread.common.image.dto.DownloadUrlResult;
import com.bread.popupbread.global.exception.BaseException;
import com.bread.popupbread.global.exception.common.InternalServerException;
import com.bread.popupbread.global.exception.common.InvalidFileNameException;
import com.bread.popupbread.global.exception.common.RequestValidationException;
import com.bread.popupbread.global.exception.common.UnsupportedMediaTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DownloadUrlService {
    private final PresingedUrlProvider presingedUrlProvider;
    private static final Set<String> UPLOAD_TYPE = Set.of("popups");
    private static final Set<String> ALLOWED_EXTENSION = Set.of("jpg", "jpeg", "png", "webp");

    public DownloadUrlResult generateDownloadUrl(String imageKey) {
        if (!imageKey.contains(".")) {
            throw new InvalidFileNameException();
        }
        String extension = imageKey.substring(imageKey.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSION.contains(extension)) {
            throw new UnsupportedMediaTypeException();
        }

        String uploadType = imageKey.substring(0, imageKey.indexOf("/"));
        if (!UPLOAD_TYPE.contains(uploadType)) {
            throw new RequestValidationException();
        }

        try {
            String presignedUrl = presingedUrlProvider.generateDownloadUrl(imageKey);
            if (presignedUrl == null || presignedUrl.isBlank()) {
                throw new InternalServerException();
            }

            return DownloadUrlResult.builder()
                    .imageKey(imageKey)
                    .downloadUrl(presignedUrl)
                    .build();
        } catch (BaseException ex) {
            throw ex;
        }

    }

    public DownloadUrlBatchResult generateDownloadUrlBatch(DownloadUrlBatchRequest req) {
        List<DownloadUrlResult> successes = new ArrayList<>();
        List<DownloadFailResult> failures = new ArrayList<>();

        for (String keyReq: req.getDownloadUrlRequests()) {
            try {
                successes.add(generateDownloadUrl(keyReq));
            } catch (BaseException ex) {
                failures.add(DownloadFailResult.builder()
                                .imageKey(keyReq)
                                .code(ex.getErrorCode().getCode())
                                .message(ex.getMessage())
                        .build());
            }
        }

        return DownloadUrlBatchResult.builder()
                .successes(successes)
                .failures(failures)
                .build();
    }
}
