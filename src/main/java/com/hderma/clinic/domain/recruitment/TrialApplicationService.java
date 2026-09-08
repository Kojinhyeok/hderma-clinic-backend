package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrialApplicationService {

    private final TrialApplicationRepository repository;

    @Transactional
    public Long apply(TrialApplicationDto.Request req) {
        if (req.getApplicantName() == null || req.getApplicantName().isBlank()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }
        if (req.getApplicantContact() == null || req.getApplicantContact().isBlank()) {
            throw new IllegalArgumentException("연락처를 입력해주세요.");
        }
        TrialApplication entity = TrialApplication.builder()
            .recruitmentId(req.getRecruitmentId())
            .memberId(req.getMemberId()) // 로그인했으면 세션값, 아니면 null (비회원 신청)
            .applicantName(req.getApplicantName())
            .applicantContact(req.getApplicantContact())
            .applicantBirth(req.getApplicantBirth())
            .build();
        repository.save(entity);
        return entity.getId();
    }

    public List<TrialApplicationDto.Response> findByRecruitment(Long recruitmentId) {
        return repository.findAllByRecruitmentIdOrderByCreatedAtDesc(recruitmentId).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    public List<TrialApplicationDto.Response> findByMember(Long memberId) {
        return repository.findAllByMemberIdOrderByCreatedAtDesc(memberId).stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        TrialApplication entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("신청 내역을 찾을 수 없습니다: " + id));
        entity.setStatus(status);
    }

    private TrialApplicationDto.Response toResponse(TrialApplication e) {
        return TrialApplicationDto.Response.builder()
            .id(e.getId()).recruitmentId(e.getRecruitmentId()).memberId(e.getMemberId())
            .applicantName(e.getApplicantName()).applicantContact(e.getApplicantContact())
            .applicantBirth(e.getApplicantBirth()).status(e.getStatus()).createdAt(e.getCreatedAt())
            .build();
    }
}