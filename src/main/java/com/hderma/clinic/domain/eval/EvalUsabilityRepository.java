package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalUsabilityRepository extends JpaRepository<EvalUsability, Long> {
    List<EvalUsability> findAllByOrderBySortOrderAsc();
}