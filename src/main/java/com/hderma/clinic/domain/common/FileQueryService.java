package com.hderma.clinic.domain.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileQueryService {

    private final FileRepository fileRepository;

    public String getThumbnailUrl(String entityType, Long entityId) {
        return fileRepository
            .findFirstByEntityTypeAndEntityIdAndFileCategory(entityType, entityId, "THUMBNAIL")
            .map(f -> f.getS3Key()) // 지금은 로컬 저장 경로를 그대로 s3Key 자리에 저장해서 씀
            .orElse(null);
    }

        /** THUMBNAIL 외 다른 카테고리(예: MARK_IMAGE)로 저장된 파일 조회용 */
    public String getThumbnailUrlByCategory(String entityType, Long entityId, String fileCategory) {
        return fileRepository
            .findFirstByEntityTypeAndEntityIdAndFileCategory(entityType, entityId, fileCategory)
            .map(f -> f.getS3Key())
            .orElse(null);
    }
}