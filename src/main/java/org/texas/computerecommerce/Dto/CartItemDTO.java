package org.texas.computerecommerce.Dto;
import lombok.Data;

@Data
public class CartItemDTO {
    private Long cartItemId;
    private ProductDTO product;  // Nested product details
    private Integer quantity;
}
