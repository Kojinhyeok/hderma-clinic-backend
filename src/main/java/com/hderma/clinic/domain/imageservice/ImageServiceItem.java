package com.hderma.clinic.domain.imageservice;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/** 이미지 서비스 페이지에 노출되는 이미지/GIF 1건 (파일 본체는 files 테이블 + S3) */
@Entity
@Table(name = "image_service_item")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ImageServiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Integer sortOrder = 0;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}