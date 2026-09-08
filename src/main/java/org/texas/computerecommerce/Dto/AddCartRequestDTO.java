package org.texas.computerecommerce.Dto;
import lombok.Data;

@Data
public class AddCartRequestDTO {
    private Long productId;
    private Integer quantity;
}