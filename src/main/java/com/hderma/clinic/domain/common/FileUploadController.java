package com.hderma.clinic.domain.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileRepository fileRepository;
    private final FileStorageService fileStorageService;

    /** 관리자 화면이 uploadUrl로 알고 PUT하는 자리 — 나중에 S3로 바뀌면 이 컨트롤러 자체가 필요 없어짐 */
    @PutMapping("/local/{fileId}")
    public ResponseEntity<Void> uploadLocal(@PathVariable Long fileId, HttpServletRequest request) throws IOException {
        FileEntity fe = fileRepository.findById(fileId)
            .orElseThrow(() -> new IllegalArgumentException("파일 정보를 찾을 수 없습니다: " + fileId));

        String path = fileStorageService.storeRaw(request.getInputStream(), fe.getEntityType(), fe.getOriginalFilename());
        fe.setS3Key(path);
        fileRepository.save(fe);

        return ResponseEntity.ok().build();
    }
}