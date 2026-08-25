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

    public List<RecruitmentDto.Response> findAll(String evalCategory) {
        var list = (evalCategory == null || evalCategory.isBlank())
            ? repository.findAllByOrderByCreatedAtDesc()
            : repository.findAllByEvalCategoryOrderByCreatedAtDesc(evalCategory);
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
            .applicationStartDate(req.getApplicationStartDate())
            .applicationEndDate(req.getApplicationEndDate())
            .participationGroup(req.getParticipationGroup())
            .requirements(req.getRequirements())
            .participationNumber(req.getParticipationNumber())
            .participationCost(req.getParticipationCost())
            .recruitmentFieldIds(req.getRecruitmentFieldIds())
            .detailContent(req.getDetailContent())
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
        entity.setApplicationStartDate(req.getApplicationStartDate());
        entity.setApplicationEndDate(req.getApplicationEndDate());
        entity.setParticipationGroup(req.getParticipationGroup());
        entity.setRequirements(req.getRequirements());
        entity.setParticipationNumber(req.getParticipationNumber());
        entity.setParticipationCost(req.getParticipationCost());
        entity.setRecruitmentFieldIds(req.getRecruitmentFieldIds());
        entity.setDetailContent(req.getDetailContent());
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
            .applicationStartDate(e.getApplicationStartDate()).applicationEndDate(e.getApplicationEndDate())
            .participationGroup(e.getParticipationGroup()).requirements(e.getRequirements())
            .participationNumber(e.getParticipationNumber()).participationCost(e.getParticipationCost())
            .recruitmentFieldIds(e.getRecruitmentFieldIds()).detailContent(e.getDetailContent())
            .createdAt(e.getCreatedAt())
            .build();
    }
}