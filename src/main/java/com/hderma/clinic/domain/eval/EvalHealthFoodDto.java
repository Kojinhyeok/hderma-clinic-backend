package com.hderma.clinic.domain.eval;

import lombok.*;

public class EvalHealthFoodDto {

    @Getter @Setter
    public static class Request {
        private String title;
        private String testPeriod;
        private String evalItems;
        private String subjectCount;
        private Integer sortOrder;
        private String originalFilename;
        private String mimeType;
        private Long fileSize;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String title;
        private String testPeriod;
        private String evalItems;
        private String subjectCount;
        private Integer sortOrder;
        private String thumbnailUrl;
    }

    @Getter @Builder
    public static class SaveResult {
        private Long id;
        private String uploadUrl;
    }
}