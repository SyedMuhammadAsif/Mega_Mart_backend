package com.megamart.orderpaymentserver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "product-service", url = "http://localhost:9096/api")
public interface ProductServiceClient {

    @GetMapping("/products/{productId}")
    ProductResponse getProductById(@PathVariable Long productId);

    @PostMapping("/products/{productId}/stock")
    Map<String, Object> updateStock(@PathVariable Long productId, @RequestBody Map<String, Integer> request);

    static class ProductResponse {
        private boolean success;
        private ProductData data;
        private String message;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public ProductData getData() { return data; }
        public void setData(ProductData data) { this.data = data; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    static class ProductData {
        private Long id;
        private String title;
        private BigDecimal price;
        private Integer stock;
        private String category;
        private String brand;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public Integer getStock() { return stock; }
        public void setStock(Integer stock) { this.stock = stock; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getBrand() { return brand; }
        public void setBrand(String brand) { this.brand = brand; }
    }
}
