package com.teamchallenge.easybuy.user.api.avatar;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarUploader {

    private final Cloudinary cloudinary;
    // Если нужно сохранять ссылку в БД, можно добавить UserRepository
    // private final UserRepository userRepository;

    private static final String AVATAR_FOLDER = "user-avatars";

    @Transactional
    public void uploadUserAvatar(final UUID userId, final MultipartFile file) {
        try {
            String publicId = "avatar-" + userId.toString();

            // Загрузка файла в Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", AVATAR_FOLDER,
                    "public_id", publicId,
                    "overwrite", true
            ));

            String url = (String) uploadResult.get("secure_url");
            log.info("Avatar uploaded to Cloudinary: {}", url);

            // TODO: Если в твоем UserEntity есть поле avatarUrl,
            // здесь нужно найти пользователя и сохранить этот url в базу:
            // UserEntity user = userRepository.findById(userId).orElseThrow();
            // user.setAvatarUrl(url);
            // userRepository.save(user);

        } catch (IOException e) {
            log.error("Failed to upload avatar for user {}", userId, e);
            throw new RuntimeException("Avatar upload failed", e);
        }
    }
}