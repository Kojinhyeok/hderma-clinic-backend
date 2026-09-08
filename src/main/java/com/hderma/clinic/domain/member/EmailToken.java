package com.hderma.clinic.domain.member;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_tokens")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class EmailToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private String email;

    @Column(length = 30)
    private String type; // INVITE (추후 VERIFICATION, PASSWORD_RESET 확장 가능)

    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}