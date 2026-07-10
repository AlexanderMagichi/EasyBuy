package com.teamchallenge.easybuy.openapi.user.api;

import com.teamchallenge.easybuy.openapi.dto.*;
import org.springframework.http.ResponseEntity;

public interface UserApi {
    ResponseEntity<UserDto> getUserProfile();

    ResponseEntity<UserDto> editUserProfile(UpdateUserAccountRequest updateUserAccountRequest);

    ResponseEntity<Void> changeUserPassword(ChangeUserPasswordRequest changeUserPasswordRequest);

    ResponseEntity<Void> deleteUserProfile();

    ResponseEntity<Void> uploadUserAvatar(org.springframework.web.multipart.MultipartFile file);

    ResponseEntity<String> getUserAvatarLink();

    ResponseEntity<Void> deleteUserAvatar();

    ResponseEntity<Void> resetUserPassword(InitiatePasswordResetRequest initiatePasswordResetRequest);

    ResponseEntity<Void> confirmResetUserPassword(ConfirmPasswordResetRequest confirmEmailRequest);
}
