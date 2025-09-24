package com.megamart.productserver.controller;

import com.megamart.productserver.dto.ProductDTO;
import com.megamart.productserver.dto.ProductSearchRequest;
import com.megamart.productserver.entity.Category;
import com.megamart.productserver.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // PRODUCT ENDPOINTS
    
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        Page<ProductDTO> products = productService.getAllProducts(page, size, sortBy, sortDirection);
        return createSuccessResponse(products, "Products retrieved successfully");
    }
    
    @GetMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
            .map(product -> createSuccessResponse(product, "Product retrieved successfully"))
            .orElse(createErrorResponse("Product not found with id: " + id, HttpStatus.NOT_FOUND));
    }
    
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return createSuccessResponse(createdProduct, "Product created successfully", HttpStatus.CREATED);
    }
    
    @PutMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return createSuccessResponse(updatedProduct, "Product updated successfully");
    }
    
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return createSuccessResponse(null, "Product deleted successfully");
    }
    
    @GetMapping("/products/category/{category}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ProductDTO> products = productService.getProductsByCategory(category, page, size);
        return createSuccessResponse(products, "Products by category retrieved successfully");
    }
    
    @GetMapping("/products/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        
        ProductSearchRequest request = new ProductSearchRequest();
        request.setQuery(keyword);
        request.setCategory(category);
        request.setBrand(brand);
        request.setMinPrice(minPrice != null ? BigDecimal.valueOf(minPrice) : null);
        request.setMaxPrice(maxPrice != null ? BigDecimal.valueOf(maxPrice) : null);
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setSortDirection(sortDirection);
        
        Page<ProductDTO> products = productService.searchProducts(request);
        return createSuccessResponse(products, "Products search completed successfully");
    }
    
    @GetMapping("/products/in-stock")
    public ResponseEntity<Map<String, Object>> getProductsInStock(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<ProductDTO> products = productService.getProductsInStock(page, size);
        return createSuccessResponse(products, "In-stock products retrieved successfully");
    }
    
    // CATEGORY ENDPOINTS
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getAllCategories() {
        List<Category> categories = productService.getAllCategoryEntities();
        return createSuccessResponse(categories, "Categories retrieved successfully");
    }
    
    @PostMapping("/categories")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody Category category) {
        Category createdCategory = productService.createCategory(category);
        return createSuccessResponse(createdCategory, "Category created successfully", HttpStatus.CREATED);
    }
    
    @PutMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category updatedCategory = productService.updateCategory(id, category);
        return createSuccessResponse(updatedCategory, "Category updated successfully");
    }
    
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, Object>> deleteCategory(@PathVariable Long id) {
        productService.deleteCategory(id);
        return createSuccessResponse(null, "Category deleted successfully");
    }
    
    @GetMapping("/brands")
    public ResponseEntity<Map<String, Object>> getAllBrands() {
        List<String> brands = productService.getAllBrands();
        return createSuccessResponse(brands, "Brands retrieved successfully");
    }
    
    @GetMapping("/brands/category/{category}")
    @CrossOrigin(origins = "http://localhost:4200")
    public ResponseEntity<Map<String, Object>> getBrandsByCategory(@PathVariable String category) {
        List<String> brands = productService.getBrandsByCategory(category);
        return createSuccessResponse(brands, "Brands for category retrieved successfully");
    }
    
    @PostMapping("/products/{id}/stock")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Map<String, Object>> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> request) {
        System.out.println("ProductController.updateStock called for product " + id);
        System.out.println("Request body: " + request);
        
        Integer stockChange = request.get("stockChange");
        if (stockChange == null) {
            return createErrorResponse("stockChange is required", HttpStatus.BAD_REQUEST);
        }
        
        ProductDTO updatedProduct = productService.updateStock(id, stockChange);
        return createSuccessResponse(updatedProduct, "Stock updated successfully");
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Product Service is running on port 9096");
    }
    
    // HELPER METHODS
    
    private ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message) {
        return createSuccessResponse(data, message, HttpStatus.OK);
    }
    
    private ResponseEntity<Map<String, Object>> createSuccessResponse(Object data, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(response);
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String error, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(status).body(response);
    }
}
