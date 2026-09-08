package com.hderma.clinic.domain.common;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileUploadLinkController {

    private final FileRepository fileRepository;
    private final S3Service s3Service;

    @Getter @Setter
    public static class UploadLinkRequest {
        private String entityType;
        private Long entityId;
        private String fileCategory;
        private String originalFilename;
        private String mimeType;
        private Long fileSize;
    }

    /** CKEditor가 이미지 삽입할 때 호출 — presigned 업로드 URL + 앞으로 계속 쓸 열람용 URL 발급 */
    @PostMapping("/upload-link")
    public ResponseEntity<Map<String, Object>> createUploadLink(@RequestBody UploadLinkRequest req) {
        String s3Key = s3Service.generateKey(req.getEntityType(), req.getOriginalFilename());

        FileEntity fe = FileEntity.builder()
            .entityType(req.getEntityType())
            .entityId(req.getEntityId())
            .fileCategory(req.getFileCategory())
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize())
            .mimeType(req.getMimeType())
            .s3Key(s3Key)
            .build();
        fileRepository.save(fe);

        String uploadUrl = s3Service.getPresignedUploadUrl(s3Key, req.getMimeType());
        String viewUrl = "/api/files/view/" + fe.getId();

        return ResponseEntity.ok(Map.of("id", fe.getId(), "uploadUrl", uploadUrl, "viewUrl", viewUrl));
    }

    /** 본문에 박제된 이미지 주소 — presigned URL은 시간 지나면 만료되니, 항상 이 안정적인 주소로 저장해두고
     *  요청 올 때마다 새 presigned URL로 302 리다이렉트 시켜줌 (그래서 절대 안 깨짐) */
    @GetMapping("/view/{id}")
    public ResponseEntity<Void> view(@PathVariable Long id) {
        FileEntity fe = fileRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다: " + id));
        String presigned = s3Service.getPresignedViewUrl(fe.getS3Key());
        return ResponseEntity.status(302).location(URI.create(presigned)).build();
    }
}