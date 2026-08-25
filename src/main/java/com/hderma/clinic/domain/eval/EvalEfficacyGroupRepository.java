package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalEfficacyGroupRepository extends JpaRepository<EvalEfficacyGroup, Long> {
    List<EvalEfficacyGroup> findAllByOrderBySortOrderAsc();
}