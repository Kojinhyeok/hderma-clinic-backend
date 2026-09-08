package com.hderma.clinic.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileQueryService {

    private final FileRepository fileRepository;
    private final S3Service s3Service;

    public String getThumbnailUrl(String entityType, Long entityId) {
        return fileRepository
            .findFirstByEntityTypeAndEntityIdAndFileCategory(entityType, entityId, "THUMBNAIL")
            .map(f -> s3Service.getPresignedViewUrl(f.getS3Key()))
            .orElse(null);
    }

    public String getThumbnailUrlByCategory(String entityType, Long entityId, String fileCategory) {
        return fileRepository
            .findFirstByEntityTypeAndEntityIdAndFileCategory(entityType, entityId, fileCategory)
            .map(f -> s3Service.getPresignedViewUrl(f.getS3Key()))
            .orElse(null);
    }
}