package com.hderma.clinic.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    List<Recruitment> findAllByOrderByCreatedAtDesc();
    List<Recruitment> findAllByEvalCategoryOrderByCreatedAtDesc(String evalCategory);
}