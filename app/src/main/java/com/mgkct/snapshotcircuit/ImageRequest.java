package com.mgkct.snapshotcircuit;

public class ImageRequest {
    private String targetImageUrl; // это поле теперь будет содержать строку Base64

    public ImageRequest(String targetImageUrl) {
        this.targetImageUrl = targetImageUrl;
    }

    public String getTargetImageUrl() {
        return targetImageUrl;
    }

    public void setTargetImageUrl(String targetImageUrl) {
        this.targetImageUrl = targetImageUrl;
    }
}