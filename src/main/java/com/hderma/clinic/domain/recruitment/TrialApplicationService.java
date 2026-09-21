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
            throw new IllegalArgumentException("회원 정보에 이름이 없습니다. 마이페이지에서 정보를 먼저 등록해주세요.");
        }
        if (req.getApplicantContact() == null || req.getApplicantContact().isBlank()) {
            throw new IllegalArgumentException("회원 정보에 연락처가 없습니다. 마이페이지에서 정보를 먼저 등록해주세요.");
        }
        if (req.getPreferredDate() == null) {
            throw new IllegalArgumentException("방문 희망 날짜를 선택해주세요.");
        }
        if (req.getPreferredTimeSlot() == null || req.getPreferredTimeSlot().isBlank()) {
            throw new IllegalArgumentException("방문 희망 시간대를 선택해주세요.");
        }
        TrialApplication entity = TrialApplication.builder()
            .recruitmentId(req.getRecruitmentId())
            .memberId(req.getMemberId()) // 컨트롤러에서 로그인 세션값으로 주입됨 (비로그인 신청은 컨트롤러 단계에서 이미 차단)
            .applicantName(req.getApplicantName())
            .applicantContact(req.getApplicantContact())
            .applicantBirth(req.getApplicantBirth())
            .preferredDate(req.getPreferredDate())
            .preferredTimeSlot(req.getPreferredTimeSlot())
            .inquiry(req.getInquiry())
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
            .applicantBirth(e.getApplicantBirth())
            .preferredDate(e.getPreferredDate()).preferredTimeSlot(e.getPreferredTimeSlot())
            .inquiry(e.getInquiry())
            .status(e.getStatus()).createdAt(e.getCreatedAt())
            .build();
    }
}