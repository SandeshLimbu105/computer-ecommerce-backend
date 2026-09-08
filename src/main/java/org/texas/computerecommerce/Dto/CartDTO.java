package org.texas.computerecommerce.Dto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CartDTO {
    private Long cartId;
    private LocalDateTime createdAt;
    private UserDTO user;  // Who owns this cart
    private List<CartItemDTO> cartItems;  // List of items
}

