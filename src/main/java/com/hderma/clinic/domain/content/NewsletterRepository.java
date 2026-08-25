package com.hderma.clinic.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsletterRepository extends JpaRepository<Newsletter, Long> {
    List<Newsletter> findAllByOrderByCreatedAtDesc();
}