package com.hderma.clinic.domain.recruitment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class RecruitmentSlotDto {

    @Getter @Setter
    public static class Request {
        private LocalDate date;
        private String time;
        private Integer capacity;
    }

    @Getter @Builder
    public static class Response {
        private Long id;
        private LocalDate date;
        private String time;
        private Integer capacity;
        private Integer booked;
        private boolean closed;
    }
}