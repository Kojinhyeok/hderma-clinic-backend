package com.hderma.clinic.domain.inquiry;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrialRequestItemRepository extends JpaRepository<TrialRequestItem, Long> {
    List<TrialRequestItem> findAllByTrialRequestId(Long trialRequestId);
}