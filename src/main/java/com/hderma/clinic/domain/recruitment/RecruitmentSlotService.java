package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentSlotService {

    private static final List<String> OCCUPYING_STATUSES = List.of("APPLIED", "SELECTED");

    private final RecruitmentSlotRepository repository;
    private final TrialApplicationRepository applicationRepository;

    /** 해당 공고에 등록된, 선택 가능한 날짜 목록 (중복 제거, 오름차순) */
    public List<LocalDate> listDates(Long recruitmentId) {
        return repository.findByRecruitmentIdOrderByDateAscTimeAsc(recruitmentId).stream()
            .map(RecruitmentSlot::getDate)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
    }

    /** 특정 날짜의 시간대 목록 (마감 여부 포함) */
    public List<RecruitmentSlotDto.Response> listByDate(Long recruitmentId, LocalDate date) {
        return repository.findByRecruitmentIdAndDateOrderByTimeAsc(recruitmentId, date).stream()
            .map(s -> toResponse(recruitmentId, s))
            .collect(Collectors.toList());
    }

    /** 관리자용: 공고의 전체 슬롯(모든 날짜) 목록 */
    public List<RecruitmentSlotDto.Response> listAll(Long recruitmentId) {
        return repository.findByRecruitmentIdOrderByDateAscTimeAsc(recruitmentId).stream()
            .map(s -> toResponse(recruitmentId, s))
            .collect(Collectors.toList());
    }

    @Transactional
    public Long create(Long recruitmentId, RecruitmentSlotDto.Request req) {
        if (req.getDate() == null) throw new IllegalArgumentException("날짜를 입력해주세요.");
        if (req.getTime() == null || req.getTime().isBlank()) throw new IllegalArgumentException("시간을 입력해주세요.");

        RecruitmentSlot entity = RecruitmentSlot.builder()
            .recruitmentId(recruitmentId)
            .date(req.getDate())
            .time(req.getTime())
            .capacity(req.getCapacity() != null ? req.getCapacity() : 1)
            .build();
        repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void delete(Long slotId) {
        repository.deleteById(slotId);
    }

    private RecruitmentSlotDto.Response toResponse(Long recruitmentId, RecruitmentSlot s) {
        long booked = applicationRepository.countByRecruitmentIdAndPreferredDateAndPreferredTimeSlotAndStatusIn(
            recruitmentId, s.getDate(), s.getTime(), OCCUPYING_STATUSES
        );
        return RecruitmentSlotDto.Response.builder()
            .id(s.getId()).date(s.getDate()).time(s.getTime()).capacity(s.getCapacity())
            .booked((int) booked)
            .closed(booked >= s.getCapacity())
            .build();
    }
}