package com.hderma.clinic.domain.irb;

import com.hderma.clinic.domain.common.FileEntity;
import com.hderma.clinic.domain.common.FileRepository;
import com.hderma.clinic.domain.member.Member;
import com.hderma.clinic.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IrbTestService {

    private static final String ENTITY_TYPE = "IRB_TEST";

    private final IrbTestRepository testRepository;
    private final IrbEmailListRepository emailListRepository;
    private final IrbSurveyResultRepository surveyResultRepository;
    private final IrbEmailListService emailListService;
    private final IrbEmailService emailService;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;

    // 임시저장 포함 전체 조회 (관리자용)
    public List<IrbDto.TestResponse> findAll() {
        List<IrbDto.TestResponse> dtos = testRepository.findAllHierarchy().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        Map<Long, List<IrbSurveyResult>> resultsByTestId = surveyResultRepository.findAll().stream()
                .filter(r -> r.getIrbTestId() != null)
                .collect(Collectors.groupingBy(IrbSurveyResult::getIrbTestId));

        dtos.forEach(dto -> {
            List<IrbSurveyResult> results = resultsByTestId.get(dto.getId());
            if (results != null && !results.isEmpty()) {
                boolean allApproved = results.stream().allMatch(r -> "APPROVED".equals(r.getReviewResult()));
                dto.setReviewStatus(allApproved ? "ALL_APPROVED" : "REVIEW_NEEDED");
            }
        });
        return dtos;
    }

    // 임시저장 제외 전체 조회 (일반 공개 목록)
    public List<IrbDto.TestResponse> findIsNotTempAll() {
        return testRepository.findAllActiveHierarchy().stream().map(this::toDto).toList();
    }

    // 상세 조회 — 관리자이거나 이메일로 초대받은 사람만 접근 가능
    public IrbDto.TestResponse findDetail(Long id, String userEmail, boolean isAdmin) {
        IrbTest irb = testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 IRB를 찾을 수 없습니다. ID: " + id));

        if (!isAdmin) {
            List<Long> accessibleIds = emailListRepository.findIrbTestIdsByEmail(userEmail);
            if (!accessibleIds.contains(irb.getIrbTestId())) {
                throw new SecurityException("접근 권한이 없습니다. 메일 수신자만 조회할 수 있습니다.");
            }
        }
        return toDto(irb);
    }

    // 원글 작성
    @Transactional
    public IrbDto.TestResponse write(IrbDto.TestRequest req) {
        IrbTest entity = IrbTest.builder()
                .memberId(req.getMemberId())
                .isTemp(req.getIsTemp() != null ? req.getIsTemp() : 0)
                .title(req.getTitle())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .categoryId(req.getCategoryId())
                .status(IrbStatus.IN_REVIEW)
                .depth(0)
                .irbCode(req.getIrbCode())
                .build();

        IrbTest saved = testRepository.save(entity);
        saved.setIrbTestId(saved.getId()); // 원글 자기 자신을 irb_test_id로
        saved = testRepository.save(saved);

        emailListService.saveEmailLog(saved.getId(), req.getEmails(), "NEW_POST");
        emailService.sendNotification(req.getEmails(),
                "[H-Derma] IRB 심사참여 요청 - " + saved.getTitle(), saved.getTitle(), saved.getId());

        return toDto(saved);
    }

    // 답글 작성
    @Transactional
    public IrbDto.TestResponse createReply(Long parentId, IrbDto.TestRequest req) {
        IrbTest parent = testRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("원글을 찾을 수 없습니다. ID: " + parentId));

        IrbTest reply = IrbTest.builder()
                .memberId(req.getMemberId())
                .isTemp(req.getIsTemp() != null ? req.getIsTemp() : 0)
                .title(req.getTitle())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .categoryId(req.getCategoryId())
                .status(IrbStatus.IN_REVIEW)
                .irbTestId(parent.getIrbTestId())
                .irbTestIdRef(parent.getId())
                .depth(parent.getDepth() + 1)
                .irbCode(req.getIrbCode())
                .build();

        IrbTest saved = testRepository.save(reply);

        emailListService.saveEmailLog(saved.getId(), req.getEmails(), "NEW_ANSWER");
        emailService.sendNotification(req.getEmails(),
                "[H-Derma] IRB 재심사참여 요청 - " + saved.getTitle(), saved.getTitle(), saved.getId());

        return toDto(saved);
    }

    // 수정 — 내용/수신자 변경 여부에 따라 알림 재발송
    @Transactional
    public IrbDto.TestResponse update(Long id, IrbDto.TestRequest req) {
        IrbTest find = testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 IRB를 찾을 수 없습니다. ID: " + id));

        boolean contentChanged = !Objects.equals(find.getTitle(), req.getTitle())
                || !Objects.equals(find.getStartDate(), req.getStartDate())
                || !Objects.equals(find.getEndDate(), req.getEndDate())
                || !Objects.equals(find.getCategoryId(), req.getCategoryId());

        find.setTitle(req.getTitle());
        find.setCategoryId(req.getCategoryId());
        find.setStartDate(req.getStartDate());
        find.setEndDate(req.getEndDate());
        if (req.getIsTemp() != null) find.setIsTemp(req.getIsTemp());
        if (req.getStatus() != null) find.setStatus(req.getStatus());
        IrbTest saved = testRepository.save(find);

        List<IrbDto.EmailRecipient> newEmails = req.getEmails() != null ? req.getEmails() : new ArrayList<>();
        List<IrbDto.EmailRecipient> addedRecipients = emailListService.getAddedRecipients(find.getIrbTestId(), newEmails);

        List<IrbEmailList> existingLogs = emailListRepository.findAllByIrbTestId(find.getId());
        String type = existingLogs.isEmpty() ? "NEW_POST" : existingLogs.get(0).getEmailType();
        emailListService.updateEmailLog(find.getIrbTestId(), newEmails, type);

        if (contentChanged || !addedRecipients.isEmpty()) {
            List<IrbDto.EmailRecipient> target = !addedRecipients.isEmpty() && !contentChanged ? addedRecipients : newEmails;
            emailService.sendNotification(target,
                    "[H-Derma] IRB 심사참여 요청 (수정) - " + find.getTitle(), find.getTitle(), find.getId());
        }

        return toDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        emailListService.delete(id);
        testRepository.deleteById(id);
    }

    private IrbDto.TestResponse toDto(IrbTest t) {
        List<String> fileUrls = fileRepository.findAll().stream()
                .filter(f -> ENTITY_TYPE.equals(f.getEntityType()) && Objects.equals(f.getEntityId(), t.getId()))
                .map(FileEntity::getS3Key)
                .toList();

        return IrbDto.TestResponse.builder()
                .id(t.getId()).memberId(t.getMemberId()).isTemp(t.getIsTemp())
                .title(t.getTitle()).startDate(t.getStartDate()).endDate(t.getEndDate())
                .irbTestId(t.getIrbTestId()).irbTestIdRef(t.getIrbTestIdRef()).depth(t.getDepth())
                .irbCode(t.getIrbCode())
                .status(t.getStatus() != null ? t.getStatus().name() : null)
                .categoryId(t.getCategoryId())
                .attachedFileUrls(fileUrls)
                .createdAt(t.getCreatedAt()).updatedAt(t.getUpdatedAt())
                .build();
    }
}