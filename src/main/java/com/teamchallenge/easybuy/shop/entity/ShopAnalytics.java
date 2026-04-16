package com.teamchallenge.easybuy.shop.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Represents the ShopAnalytics persistence entity.
 */
@Entity
@Table(name = "shop_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Aggregated shop analytics for sellers and marketplace admins")
public class ShopAnalytics {

	@Id
	@Column(name = "shop_id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "shop_id", nullable = false)
	private Shop shop;

	@Builder.Default
	@Column(name = "total_views", nullable = false)
	private long totalViews = 0L;

	@Builder.Default
	@Column(name = "unique_visitors", nullable = false)
	private long uniqueVisitors = 0L;

	@Builder.Default
	@Column(name = "total_orders", nullable = false)
	private long totalOrders = 0L;

	@Builder.Default
	@Column(name = "completed_orders", nullable = false)
	private long completedOrders = 0L;

	@Builder.Default
	@Column(name = "cancelled_orders", nullable = false)
	private long cancelledOrders = 0L;

	@Builder.Default
	@Column(name = "returned_orders", nullable = false)
	private long returnedOrders = 0L;

	@Builder.Default
	@Column(name = "total_revenue", precision = 19, scale = 2, nullable = false)
	private BigDecimal totalRevenue = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "average_order_value", precision = 19, scale = 2, nullable = false)
	private BigDecimal averageOrderValue = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "conversion_rate", precision = 7, scale = 4, nullable = false)
	private BigDecimal conversionRate = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "cancellation_rate", precision = 7, scale = 4, nullable = false)
	private BigDecimal cancellationRate = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "return_rate", precision = 7, scale = 4, nullable = false)
	private BigDecimal returnRate = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "health_score", nullable = false)
	private int healthScore = 0;

	@Builder.Default
	@Column(name = "inactive_days", nullable = false)
	private int inactiveDays = 0;

	@Builder.Default
	@Column(name = "dead_shop", nullable = false)
	private boolean deadShop = false;

	@Column(name = "period_start")
	private Instant periodStart;

	@Column(name = "period_end")
	private Instant periodEnd;

	@Column(name = "last_order_at")
	private Instant lastOrderAt;

	@Column(name = "last_activity_at")
	private Instant lastActivityAt;

	@Column(name = "last_calculated_at")
	private Instant lastCalculatedAt;

	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	public void recalculateDerivedMetrics() {
		if (totalRevenue == null) {
			totalRevenue = BigDecimal.ZERO;
		}

		conversionRate = safeRate(totalOrders, uniqueVisitors);
		cancellationRate = safeRate(cancelledOrders, totalOrders);
		returnRate = safeRate(returnedOrders, totalOrders);

		if (completedOrders > 0) {
			averageOrderValue = totalRevenue
					.divide(BigDecimal.valueOf(completedOrders), 2, RoundingMode.HALF_UP);
		} else {
			averageOrderValue = BigDecimal.ZERO;
		}

		Instant ref = lastActivityAt != null ? lastActivityAt : createdAt;
		if (ref == null) {
			inactiveDays = 0;
		} else {
			inactiveDays = (int) Math.max(0, ChronoUnit.DAYS.between(ref, Instant.now()));
		}

		healthScore = calculateHealthScore();
		deadShop = inactiveDays >= 45 && totalOrders == 0 && totalViews < 100;
		lastCalculatedAt = Instant.now();
	}

	private int calculateHealthScore() {
		int score = 100;

		if (inactiveDays > 30) score -= 25;
		if (totalOrders == 0) score -= 20;
		if (totalViews < 100) score -= 15;
		if (conversionRate.compareTo(new BigDecimal("1.00")) < 0) score -= 15;
		if (cancellationRate.compareTo(new BigDecimal("15.00")) > 0) score -= 10;
		if (returnRate.compareTo(new BigDecimal("10.00")) > 0) score -= 10;
		if (totalRevenue.compareTo(BigDecimal.ZERO) <= 0) score -= 5;

		return Math.max(0, Math.min(100, score));
	}

	private BigDecimal safeRate(long numerator, long denominator) {
		if (denominator <= 0 || numerator <= 0) {
			return BigDecimal.ZERO;
		}
		return BigDecimal.valueOf(numerator)
				.multiply(BigDecimal.valueOf(100))
				.divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
	}

	@PrePersist
	protected void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
		recalculateDerivedMetrics();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Instant.now();
		recalculateDerivedMetrics();
	}
}
