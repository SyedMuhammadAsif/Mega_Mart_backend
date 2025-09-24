package com.megamart.cartserver.client;

import lombok.Builder;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@FeignClient(name = "product-server")
public interface ProductServiceClient {

	@GetMapping("/api/products/{productId}")
	ProductServiceResponse getProductById(@PathVariable Long productId);

	@PostMapping("/api/products/{productId}/stock")
	Map<String, Object> updateStock(@PathVariable Long productId, @RequestBody Map<String, Integer> request);

	default Optional<Product> getProduct(Long productId) {
		try {
			ProductServiceResponse response = getProductById(productId);
			if (response != null && response.getData() != null) {
				ProductData data = response.getData();
				return Optional.of(Product.builder()
					.productId(data.getId())
					.name(data.getTitle())
					.price(data.getPrice())
					.stock(data.getStock())
					.build());
			}
			return Optional.empty();
		} catch (Exception e) {
			System.err.println("Error calling product service: " + e.getMessage());
			return Optional.empty();
		}
	}

	default boolean updateProductStock(Long productId, Integer stockChange) {
		try {
			Map<String, Integer> request = Map.of("stockChange", stockChange);
			Map<String, Object> response = updateStock(productId, request);
			System.out.println("Stock updated for product " + productId + " by " + stockChange);
			return true;
		} catch (Exception e) {
			System.err.println("Error updating stock: " + e.getMessage());
			return false;
		}
	}

	@Data
	static class ProductServiceResponse {
		private ProductData data;
	}

	@Data
	static class ProductData {
		private Long id;
		private String title;
		private BigDecimal price;
		private Integer stock;
	}

	@Data
	@Builder
	public static class Product {
		private Long productId;
		private String name;
		private BigDecimal price;
		private Integer stock;
	}
} 