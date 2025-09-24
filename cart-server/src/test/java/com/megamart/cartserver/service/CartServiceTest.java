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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private String userId = "1";

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
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(101L, 1);
        ProductServiceClient.ProductServiceResponse response = new ProductServiceClient.ProductServiceResponse();
        ProductServiceClient.ProductData data = new ProductServiceClient.ProductData();
        data.setId(101L);
        data.setTitle("Test Product");
        data.setPrice(new BigDecimal("10.00"));
        data.setStock(10);
        response.setData(data);

        when(productServiceClient.getProductById(101L)).thenReturn(response);
        when(productServiceClient.updateStock(eq(101L), any())).thenReturn(Map.of("success", true));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse cartResponse = cartService.addItem(userId, request);

        assertNotNull(cartResponse);
        assertEquals(1, cartResponse.getItems().size());
        assertEquals(new BigDecimal("10.00"), cartResponse.getTotal());
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

        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(101L, 2);
        ProductServiceClient.ProductServiceResponse response = new ProductServiceClient.ProductServiceResponse();
        ProductServiceClient.ProductData data = new ProductServiceClient.ProductData();
        data.setId(101L);
        data.setTitle("Test Product");
        data.setPrice(new BigDecimal("10.00"));
        data.setStock(10);
        response.setData(data);

        when(productServiceClient.getProductById(101L)).thenReturn(response);
        when(productServiceClient.updateStock(eq(101L), any())).thenReturn(Map.of("success", true));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse cartResponse = cartService.addItem(userId, request);

        assertNotNull(cartResponse);
        assertEquals(1, cartResponse.getItems().size());
        assertEquals(3, cartResponse.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("30.00"), cartResponse.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void addItem_throwsItemNotFoundException_productNotFound() {
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(999L, 1);
        when(productServiceClient.getProductById(999L)).thenReturn(null);

        assertThrows(ItemNotFoundException.class, () -> cartService.addItem(userId, request));
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void addItem_throwsInsufficientStockException() {
        CartDtos.AddItemRequest request = new CartDtos.AddItemRequest(101L, 15);
        ProductServiceClient.ProductServiceResponse response = new ProductServiceClient.ProductServiceResponse();
        ProductServiceClient.ProductData data = new ProductServiceClient.ProductData();
        data.setId(101L);
        data.setTitle("Test Product");
        data.setPrice(new BigDecimal("10.00"));
        data.setStock(10);
        response.setData(data);

        when(productServiceClient.getProductById(101L)).thenReturn(response);

        assertThrows(InsufficientStockException.class, () -> cartService.addItem(userId, request));
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

        ProductServiceClient.ProductServiceResponse response = new ProductServiceClient.ProductServiceResponse();
        ProductServiceClient.ProductData data = new ProductServiceClient.ProductData();
        data.setId(101L);
        data.setTitle("Test Product");
        data.setPrice(new BigDecimal("10.00"));
        data.setStock(10);
        response.setData(data);

        when(productServiceClient.getProductById(101L)).thenReturn(response);
        when(productServiceClient.updateStock(eq(101L), any())).thenReturn(Map.of("success", true));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse cartResponse = cartService.updateQuantity(userId, 1L, 5);

        assertNotNull(cartResponse);
        assertEquals(1, cartResponse.getItems().size());
        assertEquals(5, cartResponse.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("50.00"), cartResponse.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    void updateQuantity_throwsItemNotFoundException_cartItemNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity(userId, 999L, 5));
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

        ProductServiceClient.ProductServiceResponse response = new ProductServiceClient.ProductServiceResponse();
        ProductServiceClient.ProductData data = new ProductServiceClient.ProductData();
        data.setId(101L);
        data.setTitle("Test Product");
        data.setPrice(new BigDecimal("10.00"));
        data.setStock(10);
        response.setData(data);

        when(productServiceClient.getProductById(101L)).thenReturn(response);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity(userId, 1L, 0));
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

        ProductServiceClient.ProductServiceResponse response = new ProductServiceClient.ProductServiceResponse();
        ProductServiceClient.ProductData data = new ProductServiceClient.ProductData();
        data.setId(101L);
        data.setTitle("Test Product");
        data.setPrice(new BigDecimal("10.00"));
        data.setStock(5);
        response.setData(data);

        when(productServiceClient.getProductById(101L)).thenReturn(response);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(InsufficientStockException.class, () -> cartService.updateQuantity(userId, 1L, 10));
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

        when(productServiceClient.updateStock(eq(101L), any())).thenReturn(Map.of("success", true));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDtos.CartResponse response = cartService.removeItem(userId, 1L);

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

        when(productServiceClient.updateStock(eq(101L), any())).thenReturn(Map.of("success", true));
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.clearCart(userId);

        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.getTotal());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
} 