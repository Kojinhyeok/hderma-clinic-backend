package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalEfficacyItemRepository extends JpaRepository<EvalEfficacyItem, Long> {
    List<EvalEfficacyItem> findAllByGroupIdOrderBySortOrderAsc(Long groupId);
    void deleteAllByGroupId(Long groupId);
    long countByGroupId(Long groupId);
}