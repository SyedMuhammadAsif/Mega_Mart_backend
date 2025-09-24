package com.megamart.productserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchRequest {
    private String query;
    private String category;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String brand;
    private String sortBy = "id";
    private String sortDirection = "asc";
    private Integer page = 0;
    private Integer size = 20;
}
