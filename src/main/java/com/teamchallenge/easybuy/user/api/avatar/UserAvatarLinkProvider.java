package com.teamchallenge.easybuy.user.api.avatar;

import com.teamchallenge.easybuy.filestorage.file.FileProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for retrieving the public access link (URL) for a user's avatar image.
 * <p>
 * It interacts with the underlying file storage system to resolve the URL and
 * provides safe fallback behaviors (returning a default image link) in case
 * the avatar is missing or a storage error occurs.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAvatarLinkProvider {

    private final FileProvider fileProvider;

    /**
     * Retrieves the avatar URL for the specified user.
     * <p>
     * If the user does not have a custom avatar uploaded, or if a runtime exception
     * occurs during the storage retrieval process, this method logs the event and
     * safely falls back to a predefined default file link. This ensures that UI
     * profile requests do not fail entirely just because an image is missing or unreachable.
     *
     * @param userId the unique identifier of the user whose avatar link is being requested
     * @return the URL string pointing to the user's avatar, or "default file" if unavailable
     */
    public String getLink(final UUID userId) {
        try {
            return fileProvider.getRelatedObjectUrl(userId)
                    .orElseGet(() -> {
                        log.debug("user.avatar.not_found: userId={}", userId);
                        return "default file";
                    });
        } catch (RuntimeException exception) {
            log.error("user.avatar.error: message={}", exception.getMessage(), exception);
        }
        return "default file";
    }
}