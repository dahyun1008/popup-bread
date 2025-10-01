package com.bread.popupbread.common.image;

public interface PresingedUrlProvider {
    String generateUploadUrl(String key, String contentType);
    String generateDownloadUrl(String key);
}
