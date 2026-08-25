package com.hderma.clinic.domain.eval;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EvalEfficacyController {

    private final EvalEfficacyService service;

    @GetMapping("/api/eval-efficacy-groups")
    public List<EvalEfficacyDto.GroupResponse> listGroups() {
        return service.findAllGroups();
    }

    @PostMapping("/api/eval-efficacy-groups")
    public ResponseEntity<Map<String, Object>> createGroup(@RequestBody EvalEfficacyDto.GroupRequest req) {
        return ResponseEntity.ok(Map.of("id", service.createGroup(req)));
    }

    @PutMapping("/api/eval-efficacy-groups/{id}")
    public ResponseEntity<Void> updateGroup(@PathVariable Long id, @RequestBody EvalEfficacyDto.GroupRequest req) {
        service.updateGroup(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/eval-efficacy-groups/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        service.deleteGroup(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/eval-efficacy-groups/{groupId}/items")
    public List<EvalEfficacyDto.ItemResponse> listItems(@PathVariable Long groupId) {
        return service.findItemsByGroup(groupId);
    }

    @PostMapping("/api/eval-efficacy-groups/{groupId}/items")
    public ResponseEntity<Map<String, Object>> createItem(@PathVariable Long groupId, @RequestBody EvalEfficacyDto.ItemRequest req) {
        return ResponseEntity.ok(Map.of("id", service.createItem(groupId, req)));
    }

    @DeleteMapping("/api/eval-efficacy-items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        service.deleteItem(itemId);
        return ResponseEntity.ok().build();
    }
}