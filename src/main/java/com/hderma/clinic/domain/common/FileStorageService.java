package com.hderma.clinic.domain.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /** 파일을 저장하고 웹에서 접근 가능한 경로를 돌려줌 */
    public String store(MultipartFile file, String entityType) {
        try {
            Path dir = Paths.get(uploadDir, entityType);
            Files.createDirectories(dir);

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + entityType + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

        /** presigned URL을 흉내내는 로컬 업로드용 — 원본 파일 바이트를 그대로 받아서 저장 */
    public String storeRaw(java.io.InputStream in, String entityType, String originalFilename) {
        try {
            Path dir = Paths.get(uploadDir, entityType);
            Files.createDirectories(dir);

            String ext = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
            }
            String filename = UUID.randomUUID() + ext;
            Path target = dir.resolve(filename);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + entityType + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}