package com.hderma.clinic.domain.eval;

import lombok.*;
import java.util.List;

public class EvalEfficacyDto {
    @Getter @Setter
    public static class GroupRequest {
        private String name;
        private Integer sortOrder;
    }

    @Getter @Builder
    public static class GroupResponse {
        private Long id;
        private String name;
        private Integer sortOrder;
        private Long itemCount;
    }

    @Getter @Setter
    public static class ItemRequest {
        private String name;
        private Integer sortOrder;
    }

    @Getter @Builder
    public static class ItemResponse {
        private Long id;
        private Long groupId;
        private String name;
        private Integer sortOrder;
    }
}