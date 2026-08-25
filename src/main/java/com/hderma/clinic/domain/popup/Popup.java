package com.hderma.clinic.domain.popup;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "popup")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Popup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String linkUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private Integer width = 400;
    @Builder.Default
    private Integer posX = 50;
    @Builder.Default
    private Integer posY = 50;
    @Builder.Default
    private Integer popupOrder = 0;
    @Builder.Default
    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}