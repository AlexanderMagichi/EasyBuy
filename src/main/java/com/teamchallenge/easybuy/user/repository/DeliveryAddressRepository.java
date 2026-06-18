package com.teamchallenge.easybuy.user.repository;

import com.teamchallenge.easybuy.user.entity.DeliveryAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for managing {@link DeliveryAddressEntity} persistence.
 * Provides standard CRUD operations and custom queries for user-specific address management.
 */
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressEntity, UUID> {

    /**
     * Retrieves all delivery addresses associated with a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of delivery addresses belonging to the user
     */
    List<DeliveryAddressEntity> findAllByUserId(UUID userId);

    /**
     * Retrieves a specific delivery address by its ID and ensures it belongs to the specified user.
     * <p>
     * This acts as a security and isolation measure to prevent users from accessing or modifying
     * addresses that do not belong to their account.
     *
     * @param id     the unique identifier of the delivery address
     * @param userId the unique identifier of the user who owns the address
     * @return an {@link Optional} containing the address if found and owned by the user, otherwise empty
     */
    Optional<DeliveryAddressEntity> findByIdAndUserId(UUID id, UUID userId);

    /**
     * Clears the default flag for all delivery addresses belonging to a specific user.
     * <p>
     * This bulk update is typically executed immediately before marking a new address as the default,
     * ensuring that only one address is marked as default at any given time.
     *
     * @param userId the unique identifier of the user whose default addresses should be reset
     */
    @Modifying
    @Query("UPDATE DeliveryAddressEntity a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultForUser(@Param("userId") UUID userId);
}