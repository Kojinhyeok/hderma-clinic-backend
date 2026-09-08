package com.hderma.clinic.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    Optional<EmailToken> findByTokenAndTypeAndUsedAtIsNull(String token, String type);
    List<EmailToken> findByEmailAndTypeAndUsedAtIsNull(String email, String type);
}