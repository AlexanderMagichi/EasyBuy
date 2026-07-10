package com.teamchallenge.easybuy.security.config;

import com.teamchallenge.easybuy.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Adapter class that wraps a User entity and implements the UserDetails interface.
 * Allows integration of custom user data with Spring Security.
 */
@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final UserEntity user;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEmailVerified();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities() == null ? Collections.emptySet() : user.getAuthorities();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

}
