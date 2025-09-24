package com.megamart.cartserver.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class CartDtos {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddItemRequest {
		@NotNull
		private Long productId;
		@NotNull
		@Min(1)
		private Integer quantity;
		// Used only to compute line_total when adding
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UpdateQuantityRequest {
		@NotNull
		@Min(1)
		private Integer quantity;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CartItemResponse {
		private Long id;
		private Long productId;
		private Integer quantity;
		private BigDecimal lineTotal;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class CartResponse {
		private Long id;
		private String userId;
		private BigDecimal total;
		private List<CartItemResponse> items;

		// UI-friendly aggregate fields
		private Integer totalItems;
		private BigDecimal totalPrice;
	}
} 