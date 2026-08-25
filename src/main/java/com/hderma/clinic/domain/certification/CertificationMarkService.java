package com.hderma.clinic.domain.certification;

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
public class CertificationMarkService {

    private static final String CATEGORY_ENTITY_TYPE = "certification_mark_category";

    private final CertificationMarkRepository markRepository;
    private final CertificationMarkCategoryRepository categoryRepository;
    private final FileRepository fileRepository;
    private final FileQueryService fileQueryService;

    // ===== 대분류 =====
    public List<CertificationMarkDto.MarkResponse> findAllMarks() {
        return markRepository.findAllByOrderBySortOrderAsc().stream()
            .map(m -> CertificationMarkDto.MarkResponse.builder()
                .id(m.getId()).title(m.getTitle()).sortOrder(m.getSortOrder())
                .categoryCount(categoryRepository.countByCertificationMarkId(m.getId()))
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public Long createMark(CertificationMarkDto.MarkRequest req) {
        CertificationMark mark = CertificationMark.builder()
            .title(req.getTitle())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        markRepository.save(mark);
        return mark.getId();
    }

    @Transactional
    public void updateMark(Long id, CertificationMarkDto.MarkRequest req) {
        CertificationMark mark = markRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("대분류를 찾을 수 없습니다: " + id));
        mark.setTitle(req.getTitle());
        if (req.getSortOrder() != null) mark.setSortOrder(req.getSortOrder());
    }

    @Transactional
    public void deleteMark(Long id) {
        categoryRepository.findAllByCertificationMarkIdOrderBySortOrderAsc(id)
            .forEach(c -> fileRepository.deleteByEntityTypeAndEntityId(CATEGORY_ENTITY_TYPE, c.getId()));
        categoryRepository.deleteAllByCertificationMarkId(id);
        markRepository.deleteById(id);
    }

    // ===== 세부항목(마크) =====
    public List<CertificationMarkDto.CategoryResponse> findCategoriesByMark(Long markId) {
        return categoryRepository.findAllByCertificationMarkIdOrderBySortOrderAsc(markId).stream()
            .map(this::toCategoryResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public CertificationMarkDto.SaveResult createCategory(Long markId, CertificationMarkDto.CategoryRequest req) {
        CertificationMarkCategory category = CertificationMarkCategory.builder()
            .certificationMarkId(markId)
            .title(req.getTitle())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        categoryRepository.save(category);

        String uploadUrl = null;
        if (req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank()) {
            uploadUrl = createPendingFile(category.getId(), req);
        }
        return CertificationMarkDto.SaveResult.builder().id(category.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        fileRepository.deleteByEntityTypeAndEntityId(CATEGORY_ENTITY_TYPE, categoryId);
        categoryRepository.deleteById(categoryId);
    }

    private String createPendingFile(Long categoryId, CertificationMarkDto.CategoryRequest req) {
        FileEntity fe = FileEntity.builder()
            .entityType(CATEGORY_ENTITY_TYPE).entityId(categoryId).fileCategory("MARK_IMAGE")
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize()).mimeType(req.getMimeType())
            .build();
        fileRepository.save(fe);
        return "/api/files/local/" + fe.getId();
    }

    private CertificationMarkDto.CategoryResponse toCategoryResponse(CertificationMarkCategory c) {
        return CertificationMarkDto.CategoryResponse.builder()
            .id(c.getId()).certificationMarkId(c.getCertificationMarkId())
            .title(c.getTitle()).sortOrder(c.getSortOrder())
            .imageUrl(fileQueryService.getThumbnailUrlByCategory(CATEGORY_ENTITY_TYPE, c.getId(), "MARK_IMAGE"))
            .build();
    }
}