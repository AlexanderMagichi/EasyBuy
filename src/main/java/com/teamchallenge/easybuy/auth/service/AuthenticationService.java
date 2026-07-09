package com.teamchallenge.easybuy.auth.service;

import com.teamchallenge.easybuy.auth.dto.AuthResponseDto;
import com.teamchallenge.easybuy.auth.dto.ChangePasswordDto;
import com.teamchallenge.easybuy.auth.dto.LoginRequestDto;
import com.teamchallenge.easybuy.auth.dto.RegisterRequestDto;
import com.teamchallenge.easybuy.auth.entity.Token;
import com.teamchallenge.easybuy.infrastructure.image.CloudinaryImageService;
import com.teamchallenge.easybuy.user.entity.Authority;
import com.teamchallenge.easybuy.user.entity.UserEntity;
import com.teamchallenge.easybuy.user.entity.UserGrantedAuthority;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CloudinaryImageService cloudinaryImageService;
    private final PhoneValidationService phoneValidationService;

    public UserEntity register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByEmail(registerRequestDto.getEmail()))
            throw new IllegalStateException("The user with this email is already registered");

        if (!registerRequestDto.getPassword().equals(registerRequestDto.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }

        Authority authorityEnum;
        try {
            authorityEnum = Authority.valueOf(registerRequestDto.getRole());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role");
        }

        String avatarBaseName = (authorityEnum == Authority.SELLER && registerRequestDto.getStoreName() != null)
                ? registerRequestDto.getStoreName()
                : registerRequestDto.getEmail();

        UserEntity user = UserEntity.builder()
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .firstName("UserEntity")
                .lastName("UserEntity")
                .avatarUrl(cloudinaryImageService.generateAvatarUrl(avatarBaseName))
                .build();

        UserGrantedAuthority authority = UserGrantedAuthority.builder()
                .authority(authorityEnum)
                .build();
        user.addAuthority(authority);

        return userRepository.save(user);
    }

    public ResponseEntity<?> authenticate(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    )
            );
            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEntity not found with email : " + request.getEmail()));

            if (!user.isEmailVerified()) {
                throw new IllegalStateException("Email not confirmed");
            }
            return ResponseEntity.ok(generateToken(user));
        } catch (BadCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Incorrect login or password");
        } catch (IllegalStateException ex) {
            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .body(ex.getMessage());
        }
    }

    public AuthResponseDto generateToken(UserEntity user) {
        String roleStr = user.getAuthorities().isEmpty() ? "USER" :
                user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        String accessToken = jwtService.generateAccessToken(user.getEmail(), roleStr);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), roleStr);
        tokenService.createToken(user, refreshToken);
        return new AuthResponseDto(accessToken, refreshToken);
    }

    public AuthResponseDto refresh(String refreshToken) {
        Token token = tokenService.findByToken(refreshToken);
        if (token == null || !tokenService.isValid(token))
            throw new IllegalStateException("Invalid token");

        UserEntity user = token.getUser();
        String roleStr = user.getAuthorities().isEmpty() ? "USER" :
                user.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        String accessToken = jwtService.generateAccessToken(user.getEmail(), roleStr);
        return new AuthResponseDto(accessToken, refreshToken);
    }

    public void logout() {
        tokenService.revokedAllTokensByUser(getUser());
    }

    private UserEntity getUser() {
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(username)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "UserEntity not found: " + username));
    }

    public void changePassword(ChangePasswordDto request) {
        UserEntity user = getUser();
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public void updateAvatarUrl(String avatarUrl) {
        UserEntity user = getUser();
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
    }

    public void deleteAvatarUrl() throws IOException {
        UserEntity user = getUser();
        String avatarUrl = user.getAvatarUrl();

        if (avatarUrl != null) {
            String publicId = cloudinaryImageService.extractPublicIdFromUrl(avatarUrl);
            if (publicId != null) {
                cloudinaryImageService.deleteImage(publicId);
            }
        }

        boolean isCustomer = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

        if (isCustomer) {
            user.setAvatarUrl(cloudinaryImageService.generateAvatarUrl(user.getEmail()));
        } else {
            user.setAvatarUrl(null);
        }
        userRepository.save(user);
    }
}
