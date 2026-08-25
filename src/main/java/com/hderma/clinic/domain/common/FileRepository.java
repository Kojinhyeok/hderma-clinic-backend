package com.hderma.clinic.domain.common;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {
    Optional<FileEntity> findFirstByEntityTypeAndEntityIdAndFileCategory(
        String entityType, Long entityId, String fileCategory);
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}