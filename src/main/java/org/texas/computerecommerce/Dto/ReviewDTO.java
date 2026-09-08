package org.texas.computerecommerce.Dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long reviewId;
    private UserDTO user;  // Who wrote it
    private ProductDTO product;  // What product
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

