package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalUsabilityGroupRepository extends JpaRepository<EvalUsabilityGroup, Long> {
    List<EvalUsabilityGroup> findAllByOrderBySortOrderAsc();
}