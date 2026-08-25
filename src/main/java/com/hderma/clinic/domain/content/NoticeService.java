package com.hderma.clinic.domain.content;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;

    public List<NoticeDto.Response> findAll() {
        return repository.findAllByOrderByIsPinnedDescCreatedAtDesc().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public NoticeDto.Response findOneAndIncreaseView(Long id) {
        Notice entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id));
        entity.setViewCount(entity.getViewCount() + 1);
        return toResponse(entity);
    }

    @Transactional
    public Long create(NoticeDto.Request req) {
        Notice entity = Notice.builder()
            .title(req.getTitle())
            .content(req.getContent())
            .isPinned(req.getIsPinned() != null ? req.getIsPinned() : false)
            .build();
        repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void update(Long id, NoticeDto.Request req) {
        Notice entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + id));
        entity.setTitle(req.getTitle());
        entity.setContent(req.getContent());
        if (req.getIsPinned() != null) entity.setIsPinned(req.getIsPinned());
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private NoticeDto.Response toResponse(Notice e) {
        return NoticeDto.Response.builder()
            .id(e.getId()).title(e.getTitle()).content(e.getContent())
            .isPinned(e.getIsPinned()).viewCount(e.getViewCount()).createdAt(e.getCreatedAt())
            .build();
    }
}