package com.hderma.clinic.domain.eval;

import com.hderma.clinic.domain.common.FileEntity;
import com.hderma.clinic.domain.common.FileQueryService;
import com.hderma.clinic.domain.common.FileRepository;
import com.hderma.clinic.domain.common.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalFunctionalService {

    private static final String ENTITY_TYPE = "eval_functional";

    private final EvalFunctionalRepository repository;
    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;
    private final FileQueryService fileQueryService;

    public List<EvalFunctionalDto.Response> findAll() {
        return repository.findAllByOrderBySortOrderAsc().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public EvalFunctionalDto.SaveResult create(EvalFunctionalDto.Request req) {
        EvalFunctional entity = EvalFunctional.builder()
            .title(req.getTitle())
            .testPeriod(req.getTestPeriod())
            .evalItems(req.getEvalItems())
            .subjectCount(req.getSubjectCount())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        repository.save(entity);

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            uploadUrl = createPendingFile(entity.getId(), req);
        }
        return EvalFunctionalDto.SaveResult.builder().id(entity.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public EvalFunctionalDto.SaveResult update(Long id, EvalFunctionalDto.Request req) {
        EvalFunctional entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다: " + id));

        entity.setTitle(req.getTitle());
        entity.setTestPeriod(req.getTestPeriod());
        entity.setEvalItems(req.getEvalItems());
        entity.setSubjectCount(req.getSubjectCount());
        if (req.getSortOrder() != null) entity.setSortOrder(req.getSortOrder());

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
            uploadUrl = createPendingFile(id, req);
        }
        return EvalFunctionalDto.SaveResult.builder().id(id).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        repository.deleteById(id);
    }

    /** 파일 레코드를 미리 만들어두고, 실제 파일이 도착할 "업로드 주소"를 돌려줌 (presigned URL 흉내) */
    private String createPendingFile(Long entityId, EvalFunctionalDto.Request req) {
        FileEntity fe = FileEntity.builder()
            .entityType(ENTITY_TYPE)
            .entityId(entityId)
            .fileCategory("THUMBNAIL")
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize())
            .mimeType(req.getMimeType())
            .build();
        fileRepository.save(fe);
        return "/api/files/local/" + fe.getId();
    }

    private EvalFunctionalDto.Response toResponse(EvalFunctional e) {
        return EvalFunctionalDto.Response.builder()
            .id(e.getId())
            .title(e.getTitle())
            .testPeriod(e.getTestPeriod())
            .evalItems(e.getEvalItems())
            .subjectCount(e.getSubjectCount())
            .sortOrder(e.getSortOrder())
            .thumbnailUrl(fileQueryService.getThumbnailUrl(ENTITY_TYPE, e.getId()))
            .build();
    }
}