package com.hderma.clinic.domain.certification;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificationMarkRepository extends JpaRepository<CertificationMark, Long> {
    List<CertificationMark> findAllByOrderBySortOrderAsc();
}