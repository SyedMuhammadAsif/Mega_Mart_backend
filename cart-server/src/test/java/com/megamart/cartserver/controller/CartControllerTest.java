package com.megamart.cartserver.controller;

import com.megamart.cartserver.dto.CartDtos;
import com.megamart.cartserver.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    private Long userId; // Changed to Long
    private CartDtos.AddItemRequest addItemRequest;
    private CartDtos.CartResponse cartResponse;

    @BeforeEach
    void setUp() {
        userId = 1L; // Now correctly assigned to a Long userId
        // Removed unitPrice from AddItemRequest constructor
        addItemRequest = new CartDtos.AddItemRequest(123L, 1);
        cartResponse = CartDtos.CartResponse.builder()
                .id(1L)
                .userId(userId) // Now correctly assigned to a Long userId
                .total(BigDecimal.valueOf(1200.00))
                .totalItems(1)
                .totalPrice(BigDecimal.valueOf(1200.00))
                .items(Collections.singletonList(CartDtos.CartItemResponse.builder()
                        .id(101L)
                        .productId(123L)
                        .quantity(1)
                        .lineTotal(BigDecimal.valueOf(1200.00))
                        .build()))
                .build();
    }

    @Test
    void testAddItemToCart() throws Exception {
        when(cartService.addItem(eq(userId), any(CartDtos.AddItemRequest.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/cart/{userId}/items", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.totalPrice").value(1200.00)); // Assert on totalPrice
    }
} 