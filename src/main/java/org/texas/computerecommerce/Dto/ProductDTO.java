package org.texas.computerecommerce.Dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long productId;
    private String name;
    private String brand;
    private String description;
    private String imageUrl;
    private String specJson;
    private BigDecimal price;
    private Integer stockQty;
    private Boolean isActive;
    private CategoryDTO category;  // Nested DTO
}