package com.megamart.productserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String category;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;
    
    private Double rating = 0.0;
    
    @Column(nullable = false)
    private Integer stock = 0;
    
    private String brand;
    
    @Column(unique = true)
    private String sku;
    
    private Integer weight;
    private Double width;
    private Double height;
    private Double depth;
    
    @Column(name = "warranty_information", columnDefinition = "TEXT")
    private String warrantyInformation;
    
    @Column(name = "shipping_information", columnDefinition = "TEXT")
    private String shippingInformation;
    
    @Column(name = "availability_status")
    private String availabilityStatus = "In Stock";
    
    @Column(name = "return_policy", columnDefinition = "TEXT")
    private String returnPolicy;
    
    @Column(name = "minimum_order_quantity")
    private Integer minimumOrderQuantity = 1;
    
    @Column(length = 500)
    private String thumbnail;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductTag> tags;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductReview> reviews;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProductMetadata metadata;
}
