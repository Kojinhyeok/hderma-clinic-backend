package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvalUsabilityService {

    private final EvalUsabilityGroupRepository groupRepository;
    private final EvalUsabilityItemRepository itemRepository;

    public List<EvalUsabilityDto.GroupResponse> findAllGroups() {
        return groupRepository.findAllByOrderBySortOrderAsc().stream()
            .map(g -> EvalUsabilityDto.GroupResponse.builder()
                .id(g.getId()).name(g.getName()).sortOrder(g.getSortOrder())
                .itemCount(itemRepository.countByGroupId(g.getId()))
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public Long createGroup(EvalUsabilityDto.GroupRequest req) {
        EvalUsabilityGroup group = EvalUsabilityGroup.builder()
            .name(req.getName())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        groupRepository.save(group);
        return group.getId();
    }

    @Transactional
    public void updateGroup(Long id, EvalUsabilityDto.GroupRequest req) {
        EvalUsabilityGroup group = groupRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다: " + id));
        group.setName(req.getName());
        if (req.getSortOrder() != null) group.setSortOrder(req.getSortOrder());
    }

    @Transactional
    public void deleteGroup(Long id) {
        itemRepository.deleteAllByGroupId(id);
        groupRepository.deleteById(id);
    }

    public List<EvalUsabilityDto.ItemResponse> findItemsByGroup(Long groupId) {
        return itemRepository.findAllByGroupIdOrderBySortOrderAsc(groupId).stream()
            .map(i -> EvalUsabilityDto.ItemResponse.builder()
                .id(i.getId()).groupId(i.getGroupId()).name(i.getName()).sortOrder(i.getSortOrder())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public Long createItem(Long groupId, EvalUsabilityDto.ItemRequest req) {
        EvalUsabilityItem item = EvalUsabilityItem.builder()
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