package com.teamchallenge.easybuy.shop.repository.shopcontact;

import com.teamchallenge.easybuy.infrastructure.enums.ContactMethod;
import com.teamchallenge.easybuy.shop.entity.ShopContactInfo;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopContactInfoRepository extends JpaRepository<ShopContactInfo, UUID> {

    // ===================== BASIC =====================

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT sci FROM ShopContactInfo sci
        WHERE sci.shop.shopId = :shopId AND sci.active = true
    """)
    Optional<ShopContactInfo> findActiveByShopId(@Param("shopId") UUID shopId);

    Optional<ShopContactInfo> findByShop_ShopId(UUID shopId);

    Optional<ShopContactInfo> findByContactEmailAndActiveTrue(String email);

    Optional<ShopContactInfo> findByContactPhoneAndActiveTrue(String phone);

    // ===================== PAGINATION =====================

    Page<ShopContactInfo> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    Page<ShopContactInfo> findByVerifiedTrueAndActiveTrueOrderByVerificationDateDesc(Pageable pageable);

    @Query("""
        SELECT sci FROM ShopContactInfo sci
        WHERE sci.city = :city AND sci.country = :country
        AND sci.active = true
        ORDER BY sci.createdAt DESC
    """)
    Page<ShopContactInfo> findByLocation(@Param("city") String city,
                                         @Param("country") String country,
                                         Pageable pageable);

    // ===================== EXISTS =====================

    boolean existsByContactEmail(String email);

    boolean existsByShop_ShopIdAndActiveTrue(UUID shopId);

    boolean existsByContactEmailAndIdNot(String email, UUID excludeId);

    // ===================== ADMIN =====================

    @Query("""
        SELECT sci FROM ShopContactInfo sci
        WHERE sci.verified = false
        AND sci.active = true
        AND sci.createdAt < :beforeDate
        ORDER BY sci.createdAt ASC
    """)
    List<ShopContactInfo> findUnverifiedOlderThan(@Param("beforeDate") Instant beforeDate);

    @Modifying
    @Query("""
        UPDATE ShopContactInfo sci
        SET sci.verified = :verified,
            sci.verificationDate = :verificationDate,
            sci.updatedAt = :now
        WHERE sci.id IN :ids
    """)
    int updateVerificationStatus(@Param("ids") List<UUID> ids,
                                 @Param("verified") boolean verified,
                                 @Param("verificationDate") Instant verificationDate,
                                 @Param("now") Instant now);

    @Modifying
    @Query("""
        UPDATE ShopContactInfo sci
        SET sci.active = false,
            sci.updatedAt = :now
        WHERE sci.id IN :ids
    """)
    int deactivateContactInfo(@Param("ids") List<UUID> ids,
                              @Param("now") Instant now);

    // ===================== ANALYTICS =====================

    @Query("""
        SELECT sci.preferredContactMethod AS method, COUNT(sci) AS count
        FROM ShopContactInfo sci
        WHERE sci.active = true
        GROUP BY sci.preferredContactMethod
    """)
    List<ContactStat> getContactMethodStatistics();

    @Query("""
        SELECT sci.country AS country, COUNT(sci) AS count
        FROM ShopContactInfo sci
        WHERE sci.active = true AND sci.country IS NOT NULL
        GROUP BY sci.country
        ORDER BY COUNT(sci) DESC
    """)
    List<CountryStat> getCountryStatistics();

    // ===================== CLEANUP =====================

    @Query("""
        SELECT sci.id FROM ShopContactInfo sci
        WHERE sci.active = false AND sci.updatedAt < :beforeDate
    """)
    List<UUID> findInactiveOlderThanForCleanup(@Param("beforeDate") Instant beforeDate);

    // ===================== SPECIAL =====================

    @Query("""
        SELECT sci FROM ShopContactInfo sci
        WHERE sci.contactEmail IS NOT NULL
        AND sci.contactPhone IS NOT NULL
        AND sci.businessAddress IS NOT NULL
        AND sci.active = true
        ORDER BY sci.updatedAt DESC
    """)
    List<ShopContactInfo> findCompleteContactInfo(Pageable pageable);

    Page<ShopContactInfo> findByPreferredContactMethodAndActiveTrueOrderByCreatedAtDesc(
            ContactMethod contactMethod,
            Pageable pageable
    );

    // ===================== SECURITY =====================

    @Query("""
        SELECT sci FROM ShopContactInfo sci
        WHERE sci.shop.seller.id = :sellerId
        AND sci.active = true
    """)
    List<ShopContactInfo> findBySellerId(@Param("sellerId") UUID sellerId);

    @Query("""
        SELECT COUNT(sci) > 0 FROM ShopContactInfo sci
        WHERE sci.id = :contactId
        AND sci.shop.seller.id = :sellerId
    """)
    boolean belongsToSeller(@Param("contactId") UUID contactId,
                            @Param("sellerId") UUID sellerId);

    // ===================== COUNTERS =====================

    long countByActiveTrue();

    long countByVerifiedTrueAndActiveTrue();
}