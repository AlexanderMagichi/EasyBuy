package com.teamchallenge.easybuy.user.endpoint;

import com.teamchallenge.easybuy.email.api.EmailTokenConformer;
import com.teamchallenge.easybuy.openapi.dto.*;
import com.teamchallenge.easybuy.security.api.SecurityPrincipalProvider;
import com.teamchallenge.easybuy.user.api.ChangeUserPasswordOperationPerformer;
import com.teamchallenge.easybuy.user.api.DeleteUserOperationPerformer;
import com.teamchallenge.easybuy.user.api.SingleUserProvider;
import com.teamchallenge.easybuy.user.api.UpdateUserOperationPerformer;
import com.teamchallenge.easybuy.user.api.avatar.UserAvatarDeleter;
import com.teamchallenge.easybuy.user.api.avatar.UserAvatarLinkProvider;
import com.teamchallenge.easybuy.user.api.avatar.UserAvatarUploader;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(value = UserEndpoint.API_CUSTOMERS)
public class UserEndpoint {

    public static final String API_CUSTOMERS = "/api/v1/users";

    private final UpdateUserOperationPerformer updateUserOperationPerformer;
    private final SingleUserProvider singleUserProvider;
    private final ChangeUserPasswordOperationPerformer changeUserPasswordOperationPerformer;
    private final DeleteUserOperationPerformer deleteUserOperationPerformer;
    private final SecurityPrincipalProvider securityPrincipalProvider;
    private final UserAvatarUploader userAvatarUploader;
    private final UserAvatarLinkProvider userAvatarLinkProvider;
    private final UserAvatarDeleter userAvatarDeleter; // Добавлен сервис для удаления аватаров
    private final EmailTokenConformer emailTokenConformer;

    @GetMapping
    public ResponseEntity<UserDto> getUserProfile() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("user.profile.get: userId={}", userId);
        return ResponseEntity.ok(singleUserProvider.getUserById(userId));
    }

    @PutMapping
    public ResponseEntity<UserDto> editUserProfile(@Valid @RequestBody UpdateUserAccountRequest updateUserAccountRequest) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("user.profile.update: userId={}", userId);
        return ResponseEntity.ok(updateUserOperationPerformer.updateUser(updateUserAccountRequest));
    }

    @PatchMapping
    public ResponseEntity<Void> changeUserPassword(@Valid @RequestBody ChangeUserPasswordRequest changeUserPasswordRequest) {
        log.info("user.password.change: userId={}", securityPrincipalProvider.getUserId());
        changeUserPasswordOperationPerformer.changeUserPassword(changeUserPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteUserProfile() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("user.account.delete: userId={}", userId);
        deleteUserOperationPerformer.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/avatar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Void> uploadUserAvatar(@Validated @RequestPart("file") MultipartFile file) {
        var userId = securityPrincipalProvider.getUserId();
        log.info("user.avatar.upload: userId={}", userId);
        userAvatarUploader.uploadUserAvatar(userId, file);
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/avatar")
    public ResponseEntity<String> getUserAvatarLink() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("user.avatar.get: userId={}", userId);
        return ResponseEntity.ok(userAvatarLinkProvider.getLink(userId));
    }

    @DeleteMapping(path = "/avatar")
    public ResponseEntity<Void> deleteUserAvatar() {
        var userId = securityPrincipalProvider.getUserId();
        log.info("user.avatar.delete: userId={}", userId);
        userAvatarDeleter.delete(userId); // Исправлен вызов на новый сервис
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/password/reset")
    public ResponseEntity<Void> resetUserPassword(@Valid @RequestBody InitiatePasswordResetRequest initiatePasswordResetRequest) {
        var user = singleUserProvider.getUserByEmail(initiatePasswordResetRequest.getEmail());
        log.info("user.password.reset: userId={}", user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/password/reset/confirm")
    public ResponseEntity<Void> confirmResetUserPassword(@RequestBody final ConfirmPasswordResetRequest confirmEmailRequest) {
        log.info("user.password.reset.confirm");
        var confirmEmailReq = new ConfirmEmailRequest();
        confirmEmailReq.setToken(confirmEmailRequest.getToken());
        emailTokenConformer.confirmResetPasswordEmailByCode(confirmEmailReq);
        return ResponseEntity.ok().build();
    }

}