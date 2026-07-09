package com.teamchallenge.easybuy.user.api.avatar;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.teamchallenge.easybuy.user.exception.UserNotFoundException;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarDeleter {

    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    @Transactional
    public void delete(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null) {
            try {
                // Извлекаем public_id из URL (примерно так же, как в AuthenticationService)
                String[] parts = avatarUrl.split("/");
                String fileWithExtension = parts[parts.length - 1];
                String publicId = "user-avatars/" + fileWithExtension.split("\\.")[0];

                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (Exception e) {
                log.error("Failed to delete avatar from Cloudinary for user: {}", userId, e);
            }
        }

        user.setAvatarUrl(null);
        userRepository.save(user);
        log.info("Avatar deleted for user: {}", userId);
    }
}