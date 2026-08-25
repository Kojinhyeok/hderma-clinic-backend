package com.hderma.clinic.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentTimeTableRepository extends JpaRepository<RecruitmentTimeTable, Long> {
    List<RecruitmentTimeTable> findAllByRecruitmentId(Long recruitmentId);
    void deleteAllByRecruitmentId(Long recruitmentId);
}