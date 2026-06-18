package com.teamchallenge.easybuy.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.teamchallenge.easybuy.filestorage.FileUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryFileUploader implements FileUploader {

    private final Cloudinary cloudinary;

    @Override
    public void upload(MultipartFile file, String folder, String fileName) {
        try {
            // В Cloudinary public_id может включать путь (папку)
            String publicId = (folder != null && !folder.isEmpty()) ? folder + "/" + fileName : fileName;

            Map params = ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "auto" // Автоматически определит image или raw
            );

            cloudinary.uploader().upload(file.getBytes(), params);
            log.info("Successfully uploaded file to Cloudinary with public_id: {}", publicId);

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary: {}", e.getMessage(), e);
            throw new RuntimeException("Cloudinary upload error", e);
        }
    }
}