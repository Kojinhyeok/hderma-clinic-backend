package com.hderma.clinic.domain.eval;

import lombok.*;

public class EvalFunctionalDto {

    @Getter @Setter
    public static class Request {
        private String title;
        private String testPeriod;
        private String evalItems;
        private String subjectCount;
        private Integer sortOrder;

        // 파일이 있을 때만 관리자 화면 JS가 같이 실어 보내는 값들
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
        private String uploadUrl; // 파일 없으면 null
    }
}