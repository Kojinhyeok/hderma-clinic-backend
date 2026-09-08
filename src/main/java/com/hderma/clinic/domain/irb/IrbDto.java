package com.hderma.clinic.domain.irb;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class IrbDto {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class QuestionItem {
        private Long id;
        private String category;
        private String text;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class EmailRecipient {
        private String name;
        private String email;
    }

    @Getter @Setter
    public static class TestRequest {
        private Long memberId;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long categoryId;
        private IrbStatus status;
        private List<EmailRecipient> emails;
        private Integer isTemp;
        private Long irbTestId;
        private Long irbTestIdRef;
        private Integer depth;
        private String irbCode;
    }

    @Getter @Setter @Builder
    public static class TestResponse {
        private Long id;
        private Long memberId;
        private Integer isTemp;
        private String title;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long irbTestId;
        private Long irbTestIdRef;
        private Integer depth;
        private String irbCode;
        private String status;
        private Long categoryId;
        private List<String> attachedFileUrls;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String reviewStatus; // null, ALL_APPROVED, REVIEW_NEEDED
    }

    @Getter @Setter @Builder
    public static class CategoryResponse {
        private Long id;
        private String categoryCode;
        private String category;
    }

    @Getter @Setter
    public static class SurveyTemplateRequest {
        private Long irbTestId;
        private String surveyName;
        private List<QuestionItem> questionList;
    }

    @Getter @Setter @Builder
    public static class SurveyTemplateResponse {
        private Long id;
        private Long irbTestId;
        private String surveyName;
        private List<QuestionItem> questionList;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    public static class SurveyQuestionRequest {
        private String questionCategory;
        private String questionType;
        private String questionText;
        private Integer questionOrder;
        private Integer isActive;
    }

    @Getter @Setter @Builder
    public static class SurveyQuestionResponse {
        private Long id;
        private String questionCategory;
        private String questionType;
        private String questionText;
        private Integer questionOrder;
        private Integer isActive;
    }

    @Getter @Setter @Builder
    public static class AnswerRequest {
        private Long memberId;
        private Long irbTestId;
        private Long surveyTemplateId;
        private Map<String, Object> answerList;
    }

    @Getter @Setter @Builder
    public static class AnswerResponse {
        private Long id;
        private Long memberId;
        private Long irbTestId;
        private Long surveyTemplateId;
        private Map<String, Object> answerList;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter @Setter @Builder
    public static class ResultRequest {
        private Long memberId;
        private Long irbTestId;
        private Long surveyTemplateId;
        private String reviewResult;
        private String reviewText;
    }

    @Getter @Setter @Builder
    public static class ResultResponse {
        private Long id;
        private Long memberId;
        private Long irbTestId;
        private Long surveyTemplateId;
        private String reviewResult;
        private String reviewText;
        private LocalDateTime createdAt;
    }

    @Getter @Setter
    public static class SurveyRequest {
        private AnswerRequest answer;
        private ResultRequest result;
    }

    @Getter @Setter @Builder
    public static class SurveyResponse {
        private TestResponse test;
        private AnswerResponse answer;
        private ResultResponse result;
    }

    @Getter @Setter @Builder
    public static class EmailLogResponse {
        private Long id;
        private Long irbTestId;
        private List<EmailRecipient> userList;
        private String emailType;
        private LocalDateTime createdAt;
    }
}