package com.hderma.clinic.domain.imageservice;

import lombok.*;

public class ImageServiceDto {

    @Getter @Setter
    public static class Request {
        private String title;
        private String description;
        private Integer sortOrder;
        private String originalFilename; // 이미지/GIF 파일 (수정 시 비어 있으면 기존 파일 유지)
        private String mimeType;
        private Long fileSize;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String title;
        private String description;
        private Integer sortOrder;
        private String imageUrl;
    }

    @Getter @Builder
    public static class SaveResult {
        private Long id;
        private String uploadUrl;
    }
}