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

    // 같은 회원이 같은 시험에 이미 신청(접수/선정)했는지 확인 — 중복 신청 방지용
    boolean existsByRecruitmentIdAndMemberIdAndStatusIn(
        Long recruitmentId, Long memberId, List<String> statuses
    );
}