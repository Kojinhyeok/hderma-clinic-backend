package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalSafetyRepository extends JpaRepository<EvalSafety, Long> {
    List<EvalSafety> findAllByOrderBySortOrderAsc();
}