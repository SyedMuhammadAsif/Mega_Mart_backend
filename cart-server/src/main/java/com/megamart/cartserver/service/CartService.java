package com.megamart.cartserver.service;

import com.megamart.cartserver.dto.CartDtos;
import com.megamart.cartserver.model.Cart;
import com.megamart.cartserver.model.CartItem;
import com.megamart.cartserver.repository.CartRepository;
import com.megamart.cartserver.client.ProductServiceClient;
import com.megamart.cartserver.exception.InsufficientStockException;
import com.megamart.cartserver.exception.ItemNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final ProductServiceClient productServiceClient;

	protected Cart getOrCreateCart(String userId) {
		return cartRepository.findByUserId(userId).orElseGet(() -> {
			Cart cart = Cart.builder().userId(userId).build();
			return cartRepository.save(cart);
		});
	}

	private void recalcCartTotal(Cart cart) {
		BigDecimal total = cart.getItems().stream()
			.map(CartItem::getLineTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		cart.setTotal(total);
	}

	@Transactional
	public CartDtos.CartResponse getCart(String userId) {
		Cart cart = getOrCreateCart(userId);
		recalcCartTotal(cart);
		cartRepository.save(cart);
		return toResponse(cart);
	}

	@Transactional
	public CartDtos.CartResponse addItem(String userId, CartDtos.AddItemRequest request) {
		// Check product stock
		ProductServiceClient.Product product = productServiceClient.getProduct(request.getProductId())
			.orElseThrow(() -> new ItemNotFoundException("Product not found"));

		if (product.getStock() < request.getQuantity()) {
			throw new InsufficientStockException("Insufficient stock for product " + product.getName());
		}

		Cart cart = getOrCreateCart(userId);
		CartItem existing = cart.getItems().stream()
			.filter(i -> i.getProductId().equals(request.getProductId()))
			.findFirst().orElse(null);

		BigDecimal unitPrice = product.getPrice(); // Get unitPrice from ProductServiceClient

		if (existing != null) {
			existing.setQuantity(existing.getQuantity() + request.getQuantity());
			existing.setLineTotal(unitPrice.multiply(BigDecimal.valueOf(existing.getQuantity()))); // Recalculate based on new quantity and fetched unitPrice
		} else {
			CartItem item = CartItem.builder()
				.cart(cart)
				.productId(request.getProductId())
				.quantity(request.getQuantity())
				.lineTotal(unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())))
				.build();
			cart.getItems().add(item);
		}
		
		// Update stock in product service (reduce stock)
		System.out.println("Reducing stock for product " + request.getProductId() + " by " + request.getQuantity());
		boolean stockUpdated = productServiceClient.updateProductStock(request.getProductId(), -request.getQuantity());
		System.out.println("Stock update result: " + stockUpdated);
		
		recalcCartTotal(cart);
		cartRepository.save(cart);
		return toResponse(cart);
	}

	@Transactional
	public CartDtos.CartResponse updateQuantity(String userId, Long itemId, int quantity) {
		Cart cart = getOrCreateCart(userId);
		CartItem item = cart.getItems().stream()
			.filter(i -> i.getId().equals(itemId))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Item not found in cart"));

		// Check product stock for update
		ProductServiceClient.Product product = productServiceClient.getProduct(item.getProductId())
			.orElseThrow(() -> new ItemNotFoundException("Product not found"));

		if (product.getStock() < quantity) {
			throw new InsufficientStockException("Insufficient stock for product " + product.getName());
		}

		if (quantity < 1) throw new IllegalArgumentException("Quantity must be >= 1");
		
		// Calculate stock change needed
		int oldQuantity = item.getQuantity();
		int stockChange = oldQuantity - quantity; // Positive means return stock, negative means reduce stock
		
		// No need to calculate perUnit from item.getLineTotal if unitPrice is always fetched from product service
		BigDecimal unitPrice = product.getPrice(); // Fetch current price from product service
		item.setQuantity(quantity);
		item.setLineTotal(unitPrice.multiply(BigDecimal.valueOf(quantity))); // Recalculate line total based on current unitPrice
		
		// Update stock in product service
		System.out.println("Updating stock for product " + item.getProductId() + " by " + stockChange);
		boolean stockUpdated = productServiceClient.updateProductStock(item.getProductId(), stockChange);
		System.out.println("Stock update result: " + stockUpdated);
		
		recalcCartTotal(cart);
		cartRepository.save(cart);
		return toResponse(cart);
	}

	@Transactional
	public CartDtos.CartResponse removeItem(String userId, Long itemId) {
		Cart cart = getOrCreateCart(userId);
		
		// Find the item to get its quantity before removing
		CartItem itemToRemove = cart.getItems().stream()
			.filter(i -> i.getId().equals(itemId))
			.findFirst()
			.orElse(null);
		
		if (itemToRemove != null) {
			// Return stock to product service
			System.out.println("Returning stock for product " + itemToRemove.getProductId() + " quantity: " + itemToRemove.getQuantity());
			boolean stockUpdated = productServiceClient.updateProductStock(itemToRemove.getProductId(), itemToRemove.getQuantity());
			System.out.println("Stock return result: " + stockUpdated);
			
			// Remove item from cart
			cart.getItems().removeIf(i -> i.getId().equals(itemId));
		}
		
		recalcCartTotal(cart);
		cartRepository.save(cart);
		return toResponse(cart);
	}

	@Transactional
	public void clearCart(String userId) {
		Cart cart = getOrCreateCart(userId);
		
		// Return stock for all items
		cart.getItems().forEach(item -> {
			System.out.println("Clearing cart - returning stock for product " + item.getProductId() + " quantity: " + item.getQuantity());
			boolean stockUpdated = productServiceClient.updateProductStock(item.getProductId(), item.getQuantity());
			System.out.println("Stock return result: " + stockUpdated);
		});
		
		cart.getItems().clear();
		recalcCartTotal(cart);
		cartRepository.save(cart);
	}

	private CartDtos.CartResponse toResponse(Cart cart) {
		List<CartDtos.CartItemResponse> items = cart.getItems().stream()
			.map(ci -> CartDtos.CartItemResponse.builder()
				.id(ci.getId())
				.productId(ci.getProductId())
				.quantity(ci.getQuantity())
				.lineTotal(ci.getLineTotal())
				.build())
			.collect(Collectors.toList());

		int totalItems = items.stream().mapToInt(CartDtos.CartItemResponse::getQuantity).sum();
		BigDecimal totalPrice = items.stream()
			.map(CartDtos.CartItemResponse::getLineTotal)
			.reduce(BigDecimal.ZERO, BigDecimal::add);

		return CartDtos.CartResponse.builder()
			.id(cart.getId())
			.userId(cart.getUserId()) // Cart.getUserId() now returns Long, and CartDtos.CartResponse expects Long
			.total(cart.getTotal())
			.items(items)
			.totalItems(totalItems)
			.totalPrice(totalPrice)
			.build();
	}

	public boolean testStockUpdate(Long productId, Integer stockChange) {
		System.out.println("Testing stock update for product " + productId + " with change: " + stockChange);
		boolean result = productServiceClient.updateProductStock(productId, stockChange);
		System.out.println("Test stock update result: " + result);
		return result;
	}
} 