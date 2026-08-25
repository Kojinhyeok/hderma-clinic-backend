package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalHealthFoodRepository extends JpaRepository<EvalHealthFood, Long> {
    List<EvalHealthFood> findAllByOrderBySortOrderAsc();
}