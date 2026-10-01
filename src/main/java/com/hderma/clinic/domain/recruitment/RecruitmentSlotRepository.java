package com.hderma.clinic.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecruitmentSlotRepository extends JpaRepository<RecruitmentSlot, Long> {
    List<RecruitmentSlot> findByRecruitmentIdOrderByDateAscTimeAsc(Long recruitmentId);
    List<RecruitmentSlot> findByRecruitmentIdAndDateOrderByTimeAsc(Long recruitmentId, LocalDate date);
}