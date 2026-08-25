package com.hderma.clinic.domain.certification;

import lombok.*;

public class CertificationMarkDto {

    @Getter @Setter
    public static class MarkRequest {
        private String title;
        private Integer sortOrder;
    }

    @Getter @Builder
    public static class MarkResponse {
        private Long id;
        private String title;
        private Integer sortOrder;
        private Long categoryCount;
    }

    @Getter @Setter
    public static class CategoryRequest {
        private String title;
        private Integer sortOrder;
        private String originalFilename; // 마크 이미지
        private String mimeType;
        private Long fileSize;
    }

    @Getter @Builder
    public static class CategoryResponse {
        private Long id;
        private Long certificationMarkId;
        private String title;
        private Integer sortOrder;
        private String imageUrl;
    }

    @Getter @Builder
    public static class SaveResult {
        private Long id;
        private String uploadUrl;
    }
}