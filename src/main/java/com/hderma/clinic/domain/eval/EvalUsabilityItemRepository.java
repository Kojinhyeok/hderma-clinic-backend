package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalUsabilityItemRepository extends JpaRepository<EvalUsabilityItem, Long> {
    List<EvalUsabilityItem> findAllByGroupIdOrderBySortOrderAsc(Long groupId);
    void deleteAllByGroupId(Long groupId);
    long countByGroupId(Long groupId);
}