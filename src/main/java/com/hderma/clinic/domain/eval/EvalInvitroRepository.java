package com.hderma.clinic.domain.eval;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvalInvitroRepository extends JpaRepository<EvalInvitro, Long> {
    List<EvalInvitro> findAllByOrderBySortOrderAsc();
}