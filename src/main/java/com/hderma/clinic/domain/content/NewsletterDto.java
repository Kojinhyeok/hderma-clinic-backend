package com.hderma.clinic.domain.content;

import lombok.*;
import java.time.LocalDateTime;

public class NewsletterDto {

    @Getter @Setter
    public static class Request {
        private String title;
        private String content;
        private String originalFilename; // 썸네일용
        private String mimeType;
        private Long fileSize;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String thumbnailUrl;
        private LocalDateTime createdAt;
    }

    @Getter @Builder
    public static class SaveResult {
        private Long id;
        private String uploadUrl;
    }
}