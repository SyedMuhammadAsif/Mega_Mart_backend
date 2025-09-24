package com.megamart.cartserver.controller;

import com.megamart.cartserver.dto.CartDtos;
import com.megamart.cartserver.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

	private final CartService cartService;

	@GetMapping("/{userId}")
	public ResponseEntity<CartDtos.CartResponse> getCart(@PathVariable("userId") String userId) {
		return ResponseEntity.ok(cartService.getCart(userId));
	}

	@PostMapping("/{userId}/items")
	public ResponseEntity<CartDtos.CartResponse> addItem(@PathVariable("userId") String userId, @Valid @RequestBody CartDtos.AddItemRequest request) {
		return ResponseEntity.ok(cartService.addItem(userId, request));
	}

	@PatchMapping("/{userId}/items/{itemId}")
	public ResponseEntity<CartDtos.CartResponse> updateQuantity(
		@PathVariable("userId") String userId,
		@PathVariable("itemId") @Min(1) Long itemId,
		@Valid @RequestBody CartDtos.UpdateQuantityRequest request
	) {
		return ResponseEntity.ok(cartService.updateQuantity(userId, itemId, request.getQuantity()));
	}

	@DeleteMapping("/{userId}/items/{itemId}")
	public ResponseEntity<CartDtos.CartResponse> removeItem(@PathVariable("userId") String userId, @PathVariable("itemId") @Min(1) Long itemId) {
		return ResponseEntity.ok(cartService.removeItem(userId, itemId));
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> clear(@PathVariable("userId") String userId) {
		cartService.clearCart(userId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/test-stock/{productId}")
	public ResponseEntity<String> testStockUpdate(@PathVariable Long productId) {
		boolean result = cartService.testStockUpdate(productId, -1);
		return ResponseEntity.ok("Stock update test result: " + result);
	}

	@GetMapping("/health")
	public ResponseEntity<String> health() {
		return ResponseEntity.ok("Cart Service is running on port 8087");
	}
} 