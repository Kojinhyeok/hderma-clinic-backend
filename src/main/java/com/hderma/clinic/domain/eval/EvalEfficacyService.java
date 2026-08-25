package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalEfficacyService {

    private final EvalEfficacyGroupRepository groupRepository;
    private final EvalEfficacyItemRepository itemRepository;

    // ===== 제품군 =====
    public List<EvalEfficacyDto.GroupResponse> findAllGroups() {
        return groupRepository.findAllByOrderBySortOrderAsc().stream()
            .map(g -> EvalEfficacyDto.GroupResponse.builder()
                .id(g.getId()).name(g.getName()).sortOrder(g.getSortOrder())
                .itemCount(itemRepository.countByGroupId(g.getId()))
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public Long createGroup(EvalEfficacyDto.GroupRequest req) {
        EvalEfficacyGroup group = EvalEfficacyGroup.builder()
            .name(req.getName())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        groupRepository.save(group);
        return group.getId();
    }

    @Transactional
    public void updateGroup(Long id, EvalEfficacyDto.GroupRequest req) {
        EvalEfficacyGroup group = groupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("제품군을 찾을 수 없습니다: " + id));
        group.setName(req.getName());
        if (req.getSortOrder() != null) group.setSortOrder(req.getSortOrder());
    }

    @Transactional
    public void deleteGroup(Long id) {
        itemRepository.deleteAllByGroupId(id);
        groupRepository.deleteById(id);
    }

    // ===== 세부항목 =====
    public List<EvalEfficacyDto.ItemResponse> findItemsByGroup(Long groupId) {
        return itemRepository.findAllByGroupIdOrderBySortOrderAsc(groupId).stream()
            .map(i -> EvalEfficacyDto.ItemResponse.builder()
                .id(i.getId()).groupId(i.getGroupId()).name(i.getName()).sortOrder(i.getSortOrder())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public Long createItem(Long groupId, EvalEfficacyDto.ItemRequest req) {
        EvalEfficacyItem item = EvalEfficacyItem.builder()
            .groupId(groupId).name(req.getName())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        itemRepository.save(item);
        return item.getId();
    }

    @Transactional
    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}