package com.hderma.clinic.domain.imageservice;

import com.hderma.clinic.domain.common.FileEntity;
import com.hderma.clinic.domain.common.FileQueryService;
import com.hderma.clinic.domain.common.FileRepository;
import com.hderma.clinic.domain.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageServiceService {

    private static final String ENTITY_TYPE = "image_service";
    private static final String FILE_CATEGORY = "IMAGE";

    // 용량 제한: 큰 GIF는 브라우저가 화면을 못 따라가 깜빡임/느려짐이 생기므로 MP4로 올리도록 유도
    private static final long MAX_GIF_BYTES = 5L * 1024 * 1024;     // GIF 5MB
    private static final long MAX_IMAGE_BYTES = 10L * 1024 * 1024;  // JPG/PNG 등 10MB
    private static final long MAX_VIDEO_BYTES = 100L * 1024 * 1024; // MP4 100MB

    private final ImageServiceItemRepository repository;
    private final FileRepository fileRepository;
    private final S3Service s3Service;
    private final FileQueryService fileQueryService;

    public List<ImageServiceDto.Response> findAll() {
        return repository.findAllByOrderBySortOrderAscIdAsc().stream()
            .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ImageServiceDto.SaveResult create(ImageServiceDto.Request req) {
        validateFile(req);
        ImageServiceItem entity = ImageServiceItem.builder()
            .title(req.getTitle())
            .description(req.getDescription())
            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
            .build();
        repository.save(entity);

        String uploadUrl = null;
        if (hasFile(req)) uploadUrl = createPendingFile(entity.getId(), req);
        return ImageServiceDto.SaveResult.builder().id(entity.getId()).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public ImageServiceDto.SaveResult update(Long id, ImageServiceDto.Request req) {
        validateFile(req);
        ImageServiceItem entity = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다: " + id));
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        if (req.getSortOrder() != null) entity.setSortOrder(req.getSortOrder());

        String uploadUrl = null;
        if (hasFile(req)) {
            fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id); // 새 파일로 교체
            uploadUrl = createPendingFile(id, req);
        }
        return ImageServiceDto.SaveResult.builder().id(id).uploadUrl(uploadUrl).build();
    }

    @Transactional
    public void delete(Long id) {
        fileRepository.deleteByEntityTypeAndEntityId(ENTITY_TYPE, id);
        repository.deleteById(id);
    }

    private boolean hasFile(ImageServiceDto.Request req) {
        return req.getOriginalFilename() != null && !req.getOriginalFilename().isBlank();
    }

    /** 이미지(JPG/PNG/GIF 등) 또는 MP4만 허용 + 종류별 용량 제한 */
    private void validateFile(ImageServiceDto.Request req) {
        if (!hasFile(req)) return;

        String mime = req.getMimeType() == null ? "" : req.getMimeType().toLowerCase();
        long size = req.getFileSize() == null ? 0L : req.getFileSize();

        boolean isVideo = mime.equals("video/mp4");
        boolean isImage = mime.startsWith("image/");
        if (!isVideo && !isImage) {
            throw new IllegalArgumentException("이미지(JPG, PNG, GIF) 또는 MP4 영상 파일만 업로드할 수 있습니다.");
        }
        if (isVideo && size > MAX_VIDEO_BYTES) {
            throw new IllegalArgumentException("MP4 영상은 100MB 이하만 업로드할 수 있습니다.");
        }
        if (mime.equals("image/gif") && size > MAX_GIF_BYTES) {
            throw new IllegalArgumentException("GIF는 5MB 이하만 올릴 수 있습니다. 용량이 큰 움직이는 이미지는 MP4 영상으로 변환해서 올려주세요.");
        }
        if (isImage && !mime.equals("image/gif") && size > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException("이미지는 10MB 이하만 업로드할 수 있습니다.");
        }
    }

    private String createPendingFile(Long entityId, ImageServiceDto.Request req) {
        String s3Key = s3Service.generateKey(ENTITY_TYPE, req.getOriginalFilename());
        FileEntity fe = FileEntity.builder()
            .entityType(ENTITY_TYPE).entityId(entityId).fileCategory(FILE_CATEGORY)
            .originalFilename(req.getOriginalFilename())
            .fileSize(req.getFileSize()).mimeType(req.getMimeType())
            .s3Key(s3Key)
            .build();
        fileRepository.save(fe);
        return s3Service.getPresignedUploadUrl(s3Key, req.getMimeType());
    }

    private ImageServiceDto.Response toResponse(ImageServiceItem e) {
        return ImageServiceDto.Response.builder()
            .id(e.getId()).title(e.getTitle()).description(e.getDescription())
            .sortOrder(e.getSortOrder())
            .imageUrl(fileQueryService.getThumbnailUrlByCategory(ENTITY_TYPE, e.getId(), FILE_CATEGORY))
            .build();
    }
}