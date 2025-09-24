package com.megamart.productserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private BigDecimal price;
    private Double discountPercentage;
    private Double rating;
    private Integer stock;
    private String brand;
    private String sku;
    private String availabilityStatus;
    private String thumbnail;
    private List<String> tags;
    private List<String> images;
    private List<ProductReviewDTO> reviews;
}
