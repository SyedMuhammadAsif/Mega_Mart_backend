package com.megamart.productserver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviewDTO {
    private Long id;
    private Integer rating;
    private String comment;
    private String reviewerName;
    private String reviewerEmail;
    private LocalDateTime reviewDate;
}
