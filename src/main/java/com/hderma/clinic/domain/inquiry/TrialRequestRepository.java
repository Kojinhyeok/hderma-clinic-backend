package com.hderma.clinic.domain.inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrialRequestRepository extends JpaRepository<TrialRequest, Long> {
    List<TrialRequest> findAllByOrderByCreatedAtDesc();
}