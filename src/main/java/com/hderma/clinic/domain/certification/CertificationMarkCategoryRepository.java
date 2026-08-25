package com.hderma.clinic.domain.certification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificationMarkCategoryRepository extends JpaRepository<CertificationMarkCategory, Long> {
    List<CertificationMarkCategory> findAllByCertificationMarkIdOrderBySortOrderAsc(Long certificationMarkId);
    void deleteAllByCertificationMarkId(Long certificationMarkId);
    long countByCertificationMarkId(Long certificationMarkId);
}