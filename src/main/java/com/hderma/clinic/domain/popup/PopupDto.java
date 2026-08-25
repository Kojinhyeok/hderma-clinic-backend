package com.hderma.clinic.domain.popup;

import lombok.*;
import java.time.LocalDateTime;

public class PopupDto {

    @Getter @Setter
    public static class Request {
        private String title;
        private String linkUrl;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer width;
        private Integer posX;
        private Integer posY;
        private Integer popupOrder;
        private Boolean isActive;
        private String originalFilename; // 팝업 이미지
        private String mimeType;
        private Long fileSize;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String title;
        private String linkUrl;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Integer width;
        private Integer posX;
        private Integer posY;
        private Integer popupOrder;
        private Boolean isActive;
        private String imageUrl;
    }

    @Getter @Builder
    public static class SaveResult {
        private Long id;
        private String uploadUrl;
    }
}