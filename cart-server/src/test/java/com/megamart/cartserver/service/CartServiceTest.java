package com.megamart.cartserver.service;

import com.megamart.cartserver.client.ProductServiceClient;
import com.megamart.cartserver.dto.CartDtos;
import com.megamart.cartserver.exception.InsufficientStockException;
import com.megamart.cartserver.exception.ItemNotFoundException;
import com.megamart.cartserver.model.Cart;
import com.megamart.cartserver.model.CartItem;
import com.megamart.cartserver.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private Long userId = 1L; // Changed userId to Long

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id(1L)
                .userId(userId)
                .items(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .build();
    }

    @Test
    void getOrCreateCart_returnsExistingCart() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        Cart result = cartService.getOrCreateCart(userId);

        assertEquals(cart, result);
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void getOrCreateCart_createsNewCart() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart result = cartService.getOrCreateCart(userId);

        assertEquals(cart, result);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItem_success_newProduct() {
        // Changed AddItemRequest constructor call
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(101L, 1);
        ProductServiceClient.Product product = ProductServiceClient.Product.builder().productId(101L).name("Test Product").price(new BigDecimal("10.00")).stock(10).build();

        when(productServiceClient.getProductById(101L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse response = cartService.addItem(userId, request); // Changed userId to Long

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("10.00"), response.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItem_success_existingProduct() {
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .productId(101L)
                .quantity(1)
                .lineTotal(new BigDecimal("10.00"))
                .cart(cart)
                .build();
        cart.getItems().add(existingItem);

        // Changed AddItemRequest constructor call
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(101L, 2);
        ProductServiceClient.Product product = ProductServiceClient.Product.builder().productId(101L).name("Test Product").price(new BigDecimal("10.00")).stock(10).build();

        when(productServiceClient.getProductById(101L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse response = cartService.addItem(userId, request); // Changed userId to Long

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(3, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("30.00"), response.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItem_throwsItemNotFoundException_productNotFound() {
        // Changed AddItemRequest constructor call
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(999L, 1);
        when(productServiceClient.getProductById(999L)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> cartService.addItem(userId, request)); // Changed userId to Long
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addItem_throwsInsufficientStockException() {
        // Changed AddItemRequest constructor call
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(101L, 15);
        ProductServiceClient.Product product = ProductServiceClient.Product.builder().productId(101L).name("Test Product").price(new BigDecimal("10.00")).stock(10).build();

        when(productServiceClient.getProductById(101L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> cartService.addItem(userId, request)); // Changed userId to Long
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateQuantity_success() {
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .productId(101L)
                .quantity(1)
                .lineTotal(new BigDecimal("10.00"))
                .cart(cart)
                .build();
        cart.getItems().add(existingItem);

        ProductServiceClient.Product product = ProductServiceClient.Product.builder().productId(101L).name("Test Product").price(new BigDecimal("10.00")).stock(10).build();

        when(productServiceClient.getProductById(101L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse response = cartService.updateQuantity(userId, 1L, 5); // Changed userId to Long

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(5, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("50.00"), response.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void updateQuantity_throwsItemNotFoundException_cartItemNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity(userId, 999L, 5)); // Changed userId to Long
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateQuantity_throwsIllegalArgumentException_quantityLessThanOne() {
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .productId(101L)
                .quantity(1)
                .lineTotal(new BigDecimal("10.00"))
                .cart(cart)
                .build();
        cart.getItems().add(existingItem);

        ProductServiceClient.Product product = ProductServiceClient.Product.builder().productId(101L).name("Test Product").price(new BigDecimal("10.00")).stock(10).build();

        when(productServiceClient.getProductById(101L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity(userId, 1L, 0)); // Changed userId to Long
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void updateQuantity_throwsInsufficientStockException() {
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .productId(101L)
                .quantity(1)
                .lineTotal(new BigDecimal("10.00"))
                .cart(cart)
                .build();
        cart.getItems().add(existingItem);

        // Only 5 in stock
        ProductServiceClient.Product product = ProductServiceClient.Product.builder().productId(101L).name("Test Product").price(new BigDecimal("10.00")).stock(5).build();

        when(productServiceClient.getProductById(101L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(InsufficientStockException.class, () -> cartService.updateQuantity(userId, 1L, 10)); // Changed userId to Long
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void removeItem_success() {
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .productId(101L)
                .quantity(1)
                .lineTotal(new BigDecimal("10.00"))
                .cart(cart)
                .build();
        cart.getItems().add(existingItem);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse response = cartService.removeItem(userId, 1L); // Changed userId to Long

        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void clearCart_success() {
        CartItem existingItem = CartItem.builder()
                .id(1L)
                .productId(101L)
                .quantity(1)
                .lineTotal(new BigDecimal("10.00"))
                .cart(cart)
                .build();
        cart.getItems().add(existingItem);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.clearCart(userId); // Changed userId to Long

        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
} 