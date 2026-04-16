package com.teamchallenge.easybuy.user.entity;

import com.teamchallenge.easybuy.common.enums.AdminLevel;
import com.teamchallenge.easybuy.common.enums.AdminPermission;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.Set;

/**
 * Represents the Admin persistence entity.
 */
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "admins")
public class Admin extends User {

    @Enumerated(EnumType.STRING)
    private AdminLevel adminLevel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "admin_permissions",
            joinColumns = @JoinColumn(name = "admin_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "permission")
    private Set<AdminPermission> permissions;

    public static Admin superAdmin(String name, String email) {
        Admin admin = new Admin();
        admin.setName(name);
        admin.setEmail(email);
        admin.setRole(Role.ADMIN);
        admin.setAdminLevel(AdminLevel.SUPER_ADMIN);
        admin.setPermissions(Set.of(
                AdminPermission.USER_MANAGE,
                AdminPermission.SHOP_MODERATE,
                AdminPermission.VIEW_REPORTS
        ));
        return admin;
    }
}
