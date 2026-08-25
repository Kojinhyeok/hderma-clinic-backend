package com.hderma.clinic.domain.popup;

import com.hderma.clinic.domain.common.FileEntity;
import com.hderma.clinic.domain.common.FileQueryService;
import com.hderma.clinic.domain.common.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopupService {

    private static final String ENTITY_TYPE = "popup";

    private final PopupRepository repository;
    private final FileRepository fileRepository;
    private final FileQueryService fileQueryService;

    /** 메인페이지 진입 시 사용 — 지금 이 순간 실제로 노출돼야 하는 팝업만 골라서 줌 */
    public List<PopupDto.Response> findActiveNow() {
        LocalDateTime now = LocalDateTime.now();
        return repository.findAllByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByPopupOrderAsc(now, now)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** 관리자 화면용 — 기간 지났든 꺼져있든 전부 다 보여줌 */
    public List<PopupDto.Response> findAll() {
        return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PopupDto.SaveResult create(PopupDto.Request req) {
        Popup entity = Popup.builder()
            .title(req.getTitle())
            .linkUrl(req.getLinkUrl())
            .startDate(req.getStartDate())
            .endDate(req.getEndDate())
            .width(req.getWidth() != null ? req.getWidth() : 400)
            .posX(req.getPosX() != null ? req.getPosX() : 50)
            .posY(req.getPosY() != null ? req.getPosY() : 50)
            .popupOrder(req.getPopupOrder() != null ? req.getPopupOrder() : 0)
            .isActive(req.getIsActive() != null ? req.getIsActive() : true)
            .build();
        repository.save(entity);

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            uploadUrl = createPendingFile(entity.getId(), req);
        }
        return PopupDto.SaveResult.builder().id(entity.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public PopupDto.SaveResult update(Long id, PopupDto.Request req) {
        Popup entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("팝업을 찾을 수 없습니다: " + id));

        entity.setTitle(req.getTitle());
        entity.setLinkUrl(req.getLinkUrl());
        entity.setStartDate(req.getStartDate());
        entity.setEndDate(req.getEndDate());
        if (req.getWidth() != null) entity.setWidth(req.getWidth());
        if (req.getPosX() != null) entity.setPosX(req.getPosX());
        if (req.getPosY() != null) entity.setPosY(req.getPosY());
        if (req.getPopupOrder() != null) entity.setPopupOrder(req.getPopupOrder());
        if (req.getIsActive() != null) entity.setIsActive(req.getIsActive());

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
            uploadUrl = createPendingFile(id, req);
        }
        return PopupDto.SaveResult.builder().id(id).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        repository.deleteById(id);
    }

    private String createPendingFile(Long entityId, PopupDto.Request req) {
        FileEntity fe = FileEntity.builder()
            .entityType(ENTITY_TYPE).entityId(entityId).fileCategory("THUMBNAIL")
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize()).mimeType(req.getMimeType())
            .build();
        fileRepository.save(fe);
        return "/api/files/local/" + fe.getId();
    }

    private PopupDto.Response toResponse(Popup e) {
        return PopupDto.Response.builder()
            .id(e.getId()).title(e.getTitle()).linkUrl(e.getLinkUrl())
            .startDate(e.getStartDate()).endDate(e.getEndDate())
            .width(e.getWidth()).posX(e.getPosX()).posY(e.getPosY())
            .popupOrder(e.getPopupOrder()).isActive(e.getIsActive())
            .imageUrl(fileQueryService.getThumbnailUrl(ENTITY_TYPE, e.getId()))
            .build();
    }
}