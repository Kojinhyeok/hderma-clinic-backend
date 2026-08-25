package com.hderma.clinic.domain.recruitment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecruitmentFieldRepository extends JpaRepository<RecruitmentField, Long> {
    List<RecruitmentField> findAllByOrderBySortOrderAsc();
}