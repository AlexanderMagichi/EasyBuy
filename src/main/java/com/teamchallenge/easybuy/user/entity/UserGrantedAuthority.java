package com.teamchallenge.easybuy.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_granted_authority")
public class UserGrantedAuthority implements GrantedAuthority {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userAuthorityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "authority", nullable = false)
    private Authority authority;

    /**
     * Returns the Spring Security authority string with the {@code ROLE_} prefix
     * (e.g. {@code ROLE_SELLER}), so that {@code hasRole(...)} expressions and the
     * {@code ROLE_*} checks in the access guards resolve consistently.
     */
    public String getAuthority() {
        return "ROLE_" + authority.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserGrantedAuthority that = (UserGrantedAuthority) o;
        return authority == that.authority;
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }
}
