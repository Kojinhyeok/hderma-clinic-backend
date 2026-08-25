package com.hderma.clinic.domain.recruitment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentFieldService {

    private final RecruitmentFieldRepository repository;

    public List<RecruitmentFieldDto.Response> findAll() {
        return repository.findAllByOrderBySortOrderAsc().stream()
            .map(f -> RecruitmentFieldDto.Response.builder()
                .id(f.getId()).name(f.getName()).sortOrder(f.getSortOrder()).build())
            .collect(Collectors.toList());
    }

    @Transactional
    public Long create(RecruitmentFieldDto.Request req) {
        RecruitmentField entity = RecruitmentField.builder()
            .name(req.getName())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}