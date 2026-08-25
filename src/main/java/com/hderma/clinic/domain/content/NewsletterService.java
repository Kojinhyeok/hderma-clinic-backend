package com.hderma.clinic.domain.content;

import com.hderma.clinic.domain.common.FileEntity;
import com.hderma.clinic.domain.common.FileQueryService;
import com.hderma.clinic.domain.common.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsletterService {

    private static final String ENTITY_TYPE = "newsletter";

    private final NewsletterRepository repository;
    private final FileRepository fileRepository;
    private final FileQueryService fileQueryService;

    public List<NewsletterDto.Response> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public NewsletterDto.Response findOne(Long id) {
        return toResponse(repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("뉴스레터를 찾을 수 없습니다: " + id)));
    }

    @Transactional
    public NewsletterDto.SaveResult create(NewsletterDto.Request req) {
        Newsletter entity = Newsletter.builder()
            .title(req.getTitle()).content(req.getContent()).build();
        repository.save(entity);

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            uploadUrl = createPendingFile(entity.getId(), req);
        }
        return NewsletterDto.SaveResult.builder().id(entity.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public NewsletterDto.SaveResult update(Long id, NewsletterDto.Request req) {
        Newsletter entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("뉴스레터를 찾을 수 없습니다: " + id));
        entity.setTitle(req.getTitle());
        entity.setContent(req.getContent());

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
            uploadUrl = createPendingFile(id, req);
        }
        return NewsletterDto.SaveResult.builder().id(id).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        repository.deleteById(id);
    }

    private String createPendingFile(Long entityId, NewsletterDto.Request req) {
        FileEntity fe = FileEntity.builder()
            .entityType(ENTITY_TYPE).entityId(entityId).fileCategory("THUMBNAIL")
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize()).mimeType(req.getMimeType())
            .build();
        fileRepository.save(fe);
        return "/api/files/local/" + fe.getId();
    }

    private NewsletterDto.Response toResponse(Newsletter e) {
        return NewsletterDto.Response.builder()
            .id(e.getId()).title(e.getTitle()).content(e.getContent())
            .thumbnailUrl(fileQueryService.getThumbnailUrl(ENTITY_TYPE, e.getId()))
            .createdAt(e.getCreatedAt())
            .build();
    }
}