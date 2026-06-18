package com.teamchallenge.easybuy.user.api.avatar;

import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for generating the public access link (URL) for a user's avatar image using Cloudinary.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarLinkProvider {

    private final Cloudinary cloudinary;

    // Эти константы должны совпадать с теми, что используются в UserAvatarUploader
    private static final String AVATAR_FOLDER = "user-avatars";
    private static final String AVATAR_NAME_PREFIX = "user-avatar-";

    /**
     * Generates the Cloudinary URL for the user's avatar.
     * <p>
     * Note: Cloudinary URLs are generated based on the public_id. This method assumes
     * the image exists at that path. If the image doesn't exist, Cloudinary will
     * return a 404 on the frontend, which is standard behavior for CDN-hosted images.
     *
     * @param userId the unique identifier of the user
     * @return the URL string pointing to the user's avatar
     */
    public String getLink(final UUID userId) {
        try {
            // Формируем такой же publicId, как при загрузке: папка + префикс + ID
            String publicId = AVATAR_FOLDER + "/" + AVATAR_NAME_PREFIX + userId.toString();

            // Генерируем URL
            return cloudinary.url().generate(publicId);

        } catch (Exception e) {
            log.error("user.avatar.error: failed to generate URL for userId={}", userId, e);
            return "default file"; // Или URL на дефолтную картинку
        }
    }
}