package com.hderma.clinic.domain.recruitment;

import lombok.*;

public class RecruitmentFieldDto {

    @Getter @Setter
    public static class Request {
        private String name;
        private Integer sortOrder;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private String name;
        private Integer sortOrder;
    }
}