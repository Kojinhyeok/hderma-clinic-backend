package com.hderma.clinic.domain.content;

import lombok.*;
import java.time.LocalDateTime;

public class NoticeDto {

    @Getter @Setter
    public static class Request {
        private String title;
        private String content;
        private Boolean isPinned;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private Boolean isPinned;
        private Integer viewCount;
        private LocalDateTime createdAt;
    }
}