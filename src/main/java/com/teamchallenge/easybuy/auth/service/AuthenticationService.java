package com.teamchallenge.easybuy.auth.service;

import com.teamchallenge.easybuy.auth.entity.Token;
import com.teamchallenge.easybuy.user.entity.*;
import com.teamchallenge.easybuy.auth.dto.AuthResponseDto;
import com.teamchallenge.easybuy.auth.dto.ChangePasswordDto;
import com.teamchallenge.easybuy.auth.dto.LoginRequestDto;
import com.teamchallenge.easybuy.auth.dto.RegisterRequestDto;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import com.teamchallenge.easybuy.infrastructure.image.CloudinaryImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

/**
 * Service for handling user registration and authentication.
 * Hashes passwords, authenticates users via AuthenticationManager,
 * and generates access and refresh tokens for authenticated sessions.
 */
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

        String email = registerRequestDto.getEmail();
        String localPart = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : "user";
        String avatarSeed = registerRequestDto.getStoreName() != null && !registerRequestDto.getStoreName().isBlank()
                ? registerRequestDto.getStoreName()
                : email;

        UserEntity user = UserEntity.builder()
                .email(email)
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .firstName(localPart)
                .lastName(localPart)
                .avatarUrl(cloudinaryImageService.generateAvatarUrl(avatarSeed))
                .build();

        user.addAuthority(UserGrantedAuthority.builder()
                .authority(resolveAuthority(registerRequestDto.getRole()))
                .build());

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
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email : " + request.getEmail()));
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
        String role = getPrimaryAuthority(user).name();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), role);
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), role);
        tokenService.createToken(user, refreshToken);
        return new AuthResponseDto(accessToken, refreshToken);
    }

    public AuthResponseDto refresh(String refreshToken) {
        Token token = tokenService.findByToken(refreshToken);
        if (token == null || !tokenService.isValid(token))
            throw new IllegalStateException("Invalid token");

        UserEntity user = token.getUser();
        String accessToken = jwtService.generateAccessToken(user.getEmail(), getPrimaryAuthority(user).name());
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
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + username));
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
        String publicId = cloudinaryImageService.extractPublicIdFromUrl(avatarUrl);
        if (publicId != null) {
            cloudinaryImageService.deleteImage(publicId);
        }
        // todo make it so that depending on how the role is, the avatar is generated from the corresponding name
        if (getPrimaryAuthority(user) == Authority.CUSTOMER) {
            user.setAvatarUrl(cloudinaryImageService.generateAvatarUrl(user.getEmail()));
        } else {
            user.setAvatarUrl(null);
        }
        userRepository.save(user);
    }

    private Authority resolveAuthority(String role) {
        if (role == null || role.isBlank()) {
            return Authority.CUSTOMER;
        }

        String normalized = role.trim().toUpperCase(Locale.ROOT);
        if ("ADMIN".equals(normalized)) {
            return Authority.SUPER_ADMIN;
        }

        return Authority.valueOf(normalized);
    }

    private Authority getPrimaryAuthority(UserEntity user) {
        return Optional.ofNullable(user.getAuthorities())
                .stream()
                .flatMap(java.util.Collection::stream)
                .map(GrantedAuthority::getAuthority)
                .map(value -> value.startsWith("ROLE_") ? value.substring("ROLE_".length()) : value)
                .map(Authority::valueOf)
                .filter(authority -> authority != Authority.USER)
                .findFirst()
                .orElse(Authority.USER);
    }
}
