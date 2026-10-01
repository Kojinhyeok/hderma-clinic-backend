package com.hderma.clinic.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TrialApplicationRepository extends JpaRepository<TrialApplication, Long> {
    List<TrialApplication> findAllByRecruitmentIdOrderByCreatedAtDesc(Long recruitmentId);
    List<TrialApplication> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByRecruitmentIdAndPreferredDateAndPreferredTimeSlotAndStatusIn(
        Long recruitmentId, LocalDate preferredDate, String preferredTimeSlot, List<String> statuses
    );
}