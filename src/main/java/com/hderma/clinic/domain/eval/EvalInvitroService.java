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
public class EvalInvitroService {

    private static final String ENTITY_TYPE = "eval_invitro";

    private final EvalInvitroRepository repository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;
    private final FileQueryService fileQueryService;

    public List<EvalInvitroDto.Response> findAll() {
        return repository.findAllByOrderBySortOrderAsc().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public EvalInvitroDto.SaveResult create(EvalInvitroDto.Request req) {
        EvalInvitro entity = EvalInvitro.builder()
            .title(req.getTitle())
            .testPeriod(req.getTestPeriod())
            .evalItems(req.getEvalItems())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        repository.save(entity);

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            uploadUrl = createPendingFile(entity.getId(), req);
        }
        return EvalInvitroDto.SaveResult.builder().id(entity.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public EvalInvitroDto.SaveResult update(Long id, EvalInvitroDto.Request req) {
        EvalInvitro entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("항목을 찾을 수 없습니다: " + id));

        entity.setTitle(req.getTitle());
        entity.setTestPeriod(req.getTestPeriod());
        entity.setEvalItems(req.getEvalItems());
        if (req.getSortOrder() != null) entity.setSortOrder(req.getSortOrder());

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
            uploadUrl = createPendingFile(id, req);
        }
        return EvalInvitroDto.SaveResult.builder().id(id).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        repository.deleteById(id);
    }

    private String createPendingFile(Long entityId, EvalInvitroDto.Request req) {
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

    private EvalInvitroDto.Response toResponse(EvalInvitro e) {
        return EvalInvitroDto.Response.builder()
            .id(e.getId())
            .title(e.getTitle())
            .testPeriod(e.getTestPeriod())
            .evalItems(e.getEvalItems())
            .sortOrder(e.getSortOrder())
            .thumbnailUrl(fileQueryService.getThumbnailUrl(ENTITY_TYPE, e.getId()))
            .build();
    }
}