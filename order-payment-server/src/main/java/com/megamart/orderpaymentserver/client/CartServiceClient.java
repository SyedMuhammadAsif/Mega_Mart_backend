package com.megamart.orderpaymentserver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@FeignClient(name = "cart-service", url = "http://localhost:8087")
public interface CartServiceClient {

    @GetMapping("/cart/{userId}")
    CartResponse getCart(@PathVariable String userId);

    @DeleteMapping("/cart/{userId}")
    void clearCart(@PathVariable String userId);

    static class CartResponse {
        private Long id;
        private String userId;
        private Double total;
        private List<CartItem> items;
        private Integer totalItems;
        private Double totalPrice;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        public List<CartItem> getItems() { return items; }
        public void setItems(List<CartItem> items) { this.items = items; }
        public Integer getTotalItems() { return totalItems; }
        public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }

    static class CartItem {
        private Long id;
        private Long productId;
        private Integer quantity;
        private Double lineTotal;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getLineTotal() { return lineTotal; }
        public void setLineTotal(Double lineTotal) { this.lineTotal = lineTotal; }
    }
}
