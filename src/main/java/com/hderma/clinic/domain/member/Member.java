package com.hderma.clinic.domain.member;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 50)
    private String username;

    private String password;
    private String name;

    @Column(unique = true)
    private String email;

    private String phone;

    @Builder.Default
    @Column(length = 20)
    private String role = "MEMBER"; // MEMBER / PROFESSOR / ADMIN

    private LocalDateTime privacyAgreedAt;

    @Builder.Default
    @Column(length = 20)
    private String status = "ACTIVE"; // ACTIVE / WITHDRAWN

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}