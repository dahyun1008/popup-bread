package com.bread.popupbread.common.image;

import com.google.cloud.storage.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.concurrent.TimeUnit;

@Component
public class GcpPresingedUrlProvider implements PresingedUrlProvider {
    private final Storage storage;
    private final String bucketName;

    @Autowired
    public GcpPresingedUrlProvider(@Value("${gcp.storage.bucket}") String bucketName, Storage storage) {
        this.bucketName = bucketName;
        this.storage = storage;
    }

    public String generateUploadUrl(String key, String contentType) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, key)
                .setContentType(contentType)
                .build();

        URL url = storage.signUrl(
                blobInfo,
                5, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.withContentType()
        );

        return url.toString();
    }

    public String generateDownloadUrl(String key) {
        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, key).build();

        URL url = storage.signUrl(
                blobInfo,
                5, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        );

        return url.toString();
    }
}
