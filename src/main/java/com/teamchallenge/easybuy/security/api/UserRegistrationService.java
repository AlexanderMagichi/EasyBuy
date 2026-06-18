package com.teamchallenge.easybuy.security.api;


import com.teamchallenge.easybuy.security.converter.RegistrationDtoConverter;
import com.teamchallenge.easybuy.security.exception.UserRegistrationException;
import com.teamchallenge.easybuy.security.jwt.JwtTokenProvider;
import com.teamchallenge.easybuy.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RegistrationDtoConverter registrationDtoConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAuthenticationResponse register(final UserRegistrationRequest userRegistrationRequest) {
        String email = userRegistrationRequest.getEmail().toLowerCase(java.util.Locale.ROOT).trim();
        String encryptedPassword = passwordEncoder.encode(userRegistrationRequest.getPassword());
        UserGrantedAuthority defaultUserGrantedAuthority = UserGrantedAuthority.builder().authority(Authority.USER).build();

        UserEntity newUserEntity = registrationDtoConverter.toEntity(userRegistrationRequest);
        newUserEntity.setEmail(email);
        newUserEntity.setPassword(encryptedPassword);
        newUserEntity.addAuthority(defaultUserGrantedAuthority);
        newUserEntity.setAccountNonExpired(true);
        newUserEntity.setAccountNonLocked(true);
        newUserEntity.setCredentialsNonExpired(true);
        newUserEntity.setEnabled(true);

        try {
            UserEntity userEntity = userRepository.saveAndFlush(newUserEntity);
            final String jwtToken = jwtTokenProvider.generateToken(userEntity);
            final String jwtRefreshToken = jwtTokenProvider.generateRefreshToken(userEntity);
            UserAuthenticationResponse response = new UserAuthenticationResponse();
            response.setToken(jwtToken);
            response.setRefreshToken(jwtRefreshToken);
            // amazonq-ignore-next-line
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new UserRegistrationException("Email already registered.", e);
        }
    }
}
