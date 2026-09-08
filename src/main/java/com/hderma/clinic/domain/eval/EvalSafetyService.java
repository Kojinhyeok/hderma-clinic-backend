package com.hderma.clinic.domain.eval;

import com.hderma.clinic.domain.common.FileEntity;
import com.hderma.clinic.domain.common.FileQueryService;
import com.hderma.clinic.domain.common.FileRepository;
import com.hderma.clinic.domain.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalSafetyService {

    private static final String ENTITY_TYPE = "eval_safety";

    private final EvalSafetyRepository repository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;
    private final FileQueryService fileQueryService;

    public List<EvalSafetyDto.Response> findAll() {
        return repository.findAllByOrderBySortOrderAsc().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public EvalSafetyDto.SaveResult create(EvalSafetyDto.Request req) {
        EvalSafety entity = EvalSafety.builder()
            .title(req.getTitle())
            .testPeriod(req.getTestPeriod())
            .subjectCount(req.getSubjectCount())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        repository.save(entity);

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            uploadUrl = createPendingFile(entity.getId(), req);
        }
        return EvalSafetyDto.SaveResult.builder().id(entity.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public EvalSafetyDto.SaveResult update(Long id, EvalSafetyDto.Request req) {
        EvalSafety entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다: " + id));

        entity.setTitle(req.getTitle());
        entity.setTestPeriod(req.getTestPeriod());
        entity.setSubjectCount(req.getSubjectCount());
        if (req.getSortOrder() != null) entity.setSortOrder(req.getSortOrder());

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
            uploadUrl = createPendingFile(id, req);
        }
        return EvalSafetyDto.SaveResult.builder().id(id).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        repository.deleteById(id);
    }

    private String createPendingFile(Long entityId, EvalSafetyDto.Request req) {
        String s3Key = s3Service.generateKey(ENTITY_TYPE, req.getOriginalFilename());
        FileEntity fe = FileEntity.builder()
            .entityType(ENTITY_TYPE)
            .entityId(entityId)
            .fileCategory("THUMBNAIL")
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize())
            .mimeType(req.getMimeType())
            .s3Key(s3Key)
            .build();
        fileRepository.save(fe);
        return s3Service.getPresignedUploadUrl(s3Key, req.getMimeType());
    }

    private EvalSafetyDto.Response toResponse(EvalSafety e) {
        return EvalSafetyDto.Response.builder()
            .id(e.getId())
            .title(e.getTitle())
            .testPeriod(e.getTestPeriod())
            .subjectCount(e.getSubjectCount())
            .sortOrder(e.getSortOrder())
            .thumbnailUrl(fileQueryService.getThumbnailUrl(ENTITY_TYPE, e.getId()))
            .build();
    }
}