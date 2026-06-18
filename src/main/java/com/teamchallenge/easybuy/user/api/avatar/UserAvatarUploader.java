package com.teamchallenge.easybuy.user.api.avatar;

import com.teamchallenge.easybuy.filestorage.aws.AwsCloudfrontInvalidator;
import com.teamchallenge.easybuy.filestorage.dto.FileMetadataDto;
import com.teamchallenge.easybuy.filestorage.file.FileUploader;
import com.teamchallenge.easybuy.filestorage.filemetadata.FileMetadataDeleter;
import com.teamchallenge.easybuy.filestorage.filemetadata.FileMetadataSaver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Service responsible for orchestrating the user avatar upload process.
 * <p>
 * This service handles the complete lifecycle of an avatar update, which includes:
 * deleting old file metadata, uploading the new physical file to a designated storage bucket,
 * saving the new file metadata, and optionally invalidating the CDN cache to ensure
 * the new avatar is immediately visible to end users.
 */
@Service
@RequiredArgsConstructor
public class UserAvatarUploader {

    @Value("${spring.aws.buckets.user-avatar}")
    private String bucketName;
    private static final String AVATAR_NAME_PREFIX = "user-avatar-";

    private final FileUploader fileUploader;
    private final FileMetadataSaver fileMetadataSaver;
    private final FileMetadataDeleter fileMetadataDeleter;

    /**
     * Optional CloudFront invalidator.
     * <p>
     * Injected only if the corresponding AWS CDN configuration is active. This allows
     * the application to run smoothly in environments (like local development) where
     * a CDN is not configured or required.
     */
    @Autowired(required = false)
    private AwsCloudfrontInvalidator cloudfrontInvalidator;

    /**
     * Uploads and registers a new avatar image for the specified user.
     * <p>
     * The process follows these sequential steps within a database transaction:
     * <ol>
     * <li>Deletes any existing avatar metadata associated with the user.</li>
     * <li>Constructs a predictable file name based on the user's UUID.</li>
     * <li>Uploads the physical file to the configured cloud storage bucket.</li>
     * <li>Persists the new file metadata to the database.</li>
     * <li>Invalidates the CDN cache for the specific file name (if a CDN invalidator is configured).</li>
     * </ol>
     *
     * @param userId the unique identifier of the user updating their avatar
     * @param file   the multipart file containing the new avatar image data
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public void uploadUserAvatar(final UUID userId, final MultipartFile file) {
        fileMetadataDeleter.deleteByRelatedObjectId(userId);
        String fileName = AVATAR_NAME_PREFIX + userId.toString();
        fileUploader.upload(file, bucketName, fileName);

        FileMetadataDto fileMetadataDto = new FileMetadataDto(userId, bucketName, fileName);
        fileMetadataSaver.save(fileMetadataDto);

        if (cloudfrontInvalidator != null) {
            cloudfrontInvalidator.invalidate(fileName);
        }
    }
}