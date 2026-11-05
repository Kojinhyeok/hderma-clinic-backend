package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository repository;

    /** 홈페이지 방문자용 — 노출 ON 인 공고만 */
    public List<RecruitmentDto.Response> findAll(String evalCategory) {
        return findAll(evalCategory, false);
    }

    /** includeHidden=true 면 노출 OFF 공고까지 모두 (관리자 화면용) */
    public List<RecruitmentDto.Response> findAll(String evalCategory, boolean includeHidden) {
        boolean noCategory = (evalCategory == null || evalCategory.isBlank());
        List<Recruitment> list;
        if (includeHidden) {
            list = noCategory
                ? repository.findAllByOrderByCreatedAtDesc()
                : repository.findAllByEvalCategoryOrderByCreatedAtDesc(evalCategory);
        } else {
            list = noCategory
                ? repository.findAllByVisibleTrueOrderByCreatedAtDesc()
                : repository.findAllByEvalCategoryAndVisibleTrueOrderByCreatedAtDesc(evalCategory);
        }
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RecruitmentDto.Response findOne(Long id) {
        return toResponse(repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("모집공고를 찾을 수 없습니다: " + id)));
    }

    @Transactional
    public Long create(RecruitmentDto.Request req) {
        Recruitment entity = Recruitment.builder()
            .trialCode(req.getTrialCode())
            .trialName(req.getTrialName())
            .evalCategory(req.getEvalCategory())
            .status(req.getStatus() != null ? req.getStatus() : "OPEN")
            .startDate(req.getStartDate())
            .endDate(req.getEndDate())
            .visitCount(req.getVisitCount())
            .testSite(req.getTestSite())
            .participationGroup(req.getParticipationGroup())
            .requirements(req.getRequirements())
            .productType(req.getProductType())
            .participationCost(req.getParticipationCost())
            .recruitmentFieldIds(req.getRecruitmentFieldIds())
            .detailContent(req.getDetailContent())
            .visible(req.getVisible() != null ? req.getVisible() : Boolean.TRUE) // 새 공고는 기본 ON
            .build();
        repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void update(Long id, RecruitmentDto.Request req) {
        Recruitment entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("모집공고를 찾을 수 없습니다: " + id));
        entity.setTrialCode(req.getTrialCode());
        entity.setTrialName(req.getTrialName());
        entity.setEvalCategory(req.getEvalCategory());
        if (req.getStatus() != null) entity.setStatus(req.getStatus());
        entity.setStartDate(req.getStartDate());
        entity.setEndDate(req.getEndDate());
        entity.setVisitCount(req.getVisitCount());
        entity.setTestSite(req.getTestSite());
        entity.setParticipationGroup(req.getParticipationGroup());
        entity.setRequirements(req.getRequirements());
        entity.setProductType(req.getProductType());
        entity.setParticipationCost(req.getParticipationCost());
        entity.setRecruitmentFieldIds(req.getRecruitmentFieldIds());
        entity.setDetailContent(req.getDetailContent());
        if (req.getVisible() != null) entity.setVisible(req.getVisible()); // 보내지 않으면 기존 값 유지
    }

    /** 노출 ON/OFF 만 변경 */
    @Transactional
    public void updateVisible(Long id, boolean visible) {
        Recruitment entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("모집공고를 찾을 수 없습니다: " + id));
        entity.setVisible(visible);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    private RecruitmentDto.Response toResponse(Recruitment e) {
        return RecruitmentDto.Response.builder()
            .id(e.getId()).trialCode(e.getTrialCode()).trialName(e.getTrialName())
            .evalCategory(e.getEvalCategory()).status(e.getStatus())
            .startDate(e.getStartDate()).endDate(e.getEndDate())
            .visitCount(e.getVisitCount()).testSite(e.getTestSite())
            .participationGroup(e.getParticipationGroup()).requirements(e.getRequirements())
            .productType(e.getProductType()).participationCost(e.getParticipationCost())
            .recruitmentFieldIds(e.getRecruitmentFieldIds()).detailContent(e.getDetailContent())
            .visible(e.getVisible() == null || e.getVisible()) // 값이 없으면 ON 으로 간주
            .createdAt(e.getCreatedAt())
            .build();
    }
}