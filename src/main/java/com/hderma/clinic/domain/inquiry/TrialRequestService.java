package com.hderma.clinic.domain.inquiry;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrialRequestService {

    private final TrialRequestRepository repository;
    private final TrialRequestItemRepository itemRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long create(TrialRequestDto.Request req) {
        // 허니팟에 값이 있으면 봇 — 에러 없이 조용히 무시 (봇에게 힌트를 주지 않기 위함)
        if (req.getWebsite() != null && !req.getWebsite().isBlank()) {
            return null;
        }

        if (req.getCompanyName() == null || req.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("회사명을 입력해주세요.");
        }
        if (req.getPassword() == null || req.getPassword().isBlank()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }
        if (req.getPrivacyAgreed() == null || !req.getPrivacyAgreed()) {
            throw new IllegalArgumentException("개인정보 수집 및 이용에 동의해주세요.");
        }

        TrialRequest entity = TrialRequest.builder()
            .companyName(req.getCompanyName())
            .businessRegNo(req.getBusinessRegNo())
            .managerName(req.getManagerName())
            .managerTitle(req.getManagerTitle())
            .contact(req.getContact())
            .email(req.getEmail())
            .productType(req.getProductType())
            .desiredStartDate(req.getDesiredStartDate())
            .desiredReportDate(req.getDesiredReportDate())
            .consultType(req.getConsultType())
            .content(req.getContent())
            .password(passwordEncoder.encode(req.getPassword()))
            .privacyAgreed(true)
            .build();
        repository.save(entity);

        if (req.getItems() != null) {
            for (TrialRequestDto.ItemInput item : req.getItems()) {
                itemRepository.save(TrialRequestItem.builder()
                    .trialRequestId(entity.getId())
                    .evalSource(item.getCategory())
                    .itemNameSnapshot(item.getName())
                    .build());
            }
        }

        return entity.getId();
    }

    public List<TrialRequestDto.Response> findAll() {
        return repository.findAllByOrderByCreatedAtDesc().stream()
            .map(e -> toResponse(e, false))
            .collect(Collectors.toList());
    }

    public TrialRequestDto.Response findOne(Long id) {
        TrialRequest entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다: " + id));
        return toResponse(entity, true);
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        TrialRequest entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("문의를 찾을 수 없습니다: " + id));
        entity.setStatus(status);
    }

    @Transactional
    public void delete(Long id) {
        itemRepository.findAllByTrialRequestId(id).forEach(i -> itemRepository.deleteById(i.getId()));
        repository.deleteById(id);
    }

    private TrialRequestDto.Response toResponse(TrialRequest e, boolean withItems) {
        var builder = TrialRequestDto.Response.builder()
            .id(e.getId())
            .companyName(e.getCompanyName())
            .managerName(e.getManagerName())
            .contact(e.getContact())
            .email(e.getEmail())
            .productType(e.getProductType())
            .desiredStartDate(e.getDesiredStartDate())
            .desiredReportDate(e.getDesiredReportDate())
            .consultType(e.getConsultType())
            .content(e.getContent())
            .status(e.getStatus())
            .createdAt(e.getCreatedAt());

        if (withItems) {
            List<TrialRequestDto.ItemResponse> items = itemRepository.findAllByTrialRequestId(e.getId()).stream()
                .map(i -> TrialRequestDto.ItemResponse.builder()
                    .category(i.getEvalSource())
                    .name(i.getItemNameSnapshot())
                    .build())
                .collect(Collectors.toList());
            builder.items(items);
        }

        return builder.build();
    }
}