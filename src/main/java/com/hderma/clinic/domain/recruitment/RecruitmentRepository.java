package com.hderma.clinic.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    // 관리자용 (노출 ON/OFF 모두 포함)
    List<Recruitment> findAllByOrderByCreatedAtDesc();
    List<Recruitment> findAllByEvalCategoryOrderByCreatedAtDesc(String evalCategory);

    // 홈페이지 방문자용 (노출 ON 인 공고만)
    List<Recruitment> findAllByVisibleTrueOrderByCreatedAtDesc();
    List<Recruitment> findAllByEvalCategoryAndVisibleTrueOrderByCreatedAtDesc(String evalCategory);
}