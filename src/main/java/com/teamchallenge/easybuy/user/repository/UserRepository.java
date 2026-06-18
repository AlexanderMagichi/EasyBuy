package com.teamchallenge.easybuy.user.repository;

import com.teamchallenge.easybuy.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for managing {@link UserEntity} persistence.
 * <p>
 * Provides standard CRUD operations and custom optimizing queries for authentication,
 * password management, and account locking mechanisms.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Retrieves a user entity by their unique email address.
     * <p>
     * Frequently used during the authentication process and user lookups.
     *
     * @param email the email address of the user
     * @return an {@link Optional} containing the user entity if found, otherwise empty
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Directly updates the password for a specific user in the database.
     * <p>
     * Using a bulk modifying query prevents the need to load the entire user entity
     * into the persistence context just to change the password, improving performance.
     *
     * @param newPassword the new encoded password to set
     * @param userId      the unique identifier of the user
     */
    @Modifying
    @Query(value = "UPDATE UserEntity u SET u.password = :newPassword WHERE u.id = :userId")
    void changeUserPassword(@Param("newPassword") String newPassword, @Param("userId") UUID userId);

    /**
     * Updates the lock status of a user's account based on their email address.
     * <p>
     * Typically invoked by authentication failure listeners (to lock the account after
     * too many failed attempts) or administrators.
     *
     * @param email            the email address of the target user
     * @param accountNonLocked the new lock status ({@code true} = unlocked, {@code false} = locked)
     * @return the number of rows affected by the update query
     */
    @Modifying
    @Query("UPDATE UserEntity u SET u.accountNonLocked = :accountNonLocked WHERE u.email = :email")
    int setAccountLockedStatus(@Param("email") String email, @Param("accountNonLocked") boolean accountNonLocked);

    /**
     * Unlocks all user accounts that are eligible for unlocking.
     * <p>
     * This batch operation evaluates the {@code LoginAttemptEntity} table to find users
     * whose lockout periods have expired. It is typically executed by a scheduled background cron job.
     */
    @Modifying
    @Query(value = "UPDATE UserEntity u SET u.accountNonLocked = true WHERE u.email IN (SELECT la.userEmail FROM LoginAttemptEntity la WHERE la.isUserLocked = false AND la.expirationDatetime IS NOT NULL)")
    void unlockUsers();
}