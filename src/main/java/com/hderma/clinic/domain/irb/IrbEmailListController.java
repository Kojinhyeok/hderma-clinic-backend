package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irb-emails")
@RequiredArgsConstructor
public class IrbEmailListController {

    private final IrbEmailListService emailListService;

    @GetMapping("/logs/{irbTestId}")
    public ResponseEntity<List<IrbDto.EmailLogResponse>> getEmailLogs(@PathVariable Long irbTestId) {
        return ResponseEntity.ok(emailListService.getLogsByTestId(irbTestId));
    }

    @PostMapping("/logs/{irbTestId}")
    public ResponseEntity<IrbDto.EmailLogResponse> createEmailLog(
            @PathVariable Long irbTestId,
            @RequestBody List<IrbDto.EmailRecipient> users,
            @RequestParam String type) {
        return ResponseEntity.ok(emailListService.saveEmailLog(irbTestId, users, type));
    }

    @DeleteMapping("/{irbId}")
    public ResponseEntity<Void> deleteEmailLogs(@PathVariable Long irbId) {
        emailListService.delete(irbId);
        return ResponseEntity.noContent().build();
    }
}