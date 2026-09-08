package com.hderma.clinic.domain.irb;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "irb_email_list")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IrbEmailList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "irb_test_id")
    private Long irbTestId;

    @Convert(converter = IrbEmailListConverter.class)
    @Column(name = "user_list", columnDefinition = "json")
    private List<IrbDto.EmailRecipient> userList;

    @Column(name = "email_type", length = 30)
    private String emailType; // NEW_ANSWER, NEW_POST, ANSWER_COMPLETE

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}