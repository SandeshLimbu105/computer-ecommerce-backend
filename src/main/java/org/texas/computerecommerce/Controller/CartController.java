package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.*;
import org.texas.computerecommerce.Entity.Cart;
import org.texas.computerecommerce.Entity.CartItem;
import org.texas.computerecommerce.Security.SecurityUtils;
import org.texas.computerecommerce.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        // ✅ FIX (bug #1): only the cart's own owner (or an admin) may view it.
        SecurityUtils.requireSelfOrAdmin(userId);
        Cart cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDTO> addItemToCart(
            @PathVariable Long userId,
            @RequestBody AddCartRequestDTO request) {
        // ✅ FIX (bug #1): only the cart's own owner (or admin) may add items.
        SecurityUtils.requireSelfOrAdmin(userId);
        Cart cart = cartService.addItemToCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity) {
        // ✅ FIX (bug #1): ownership checked against the cart item's actual owner.
        SecurityUtils.requireSelfOrAdmin(cartService.getOwnerUserId(cartItemId));
        Cart cart = cartService.updateCartItem(cartItemId, quantity);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDTO> removeItemFromCart(@PathVariable Long cartItemId) {
        // ✅ FIX (bug #1): ownership checked against the cart item's actual owner.
        SecurityUtils.requireSelfOrAdmin(cartService.getOwnerUserId(cartItemId));
        Cart cart = cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok(convertToDTO(cart));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        // ✅ FIX (bug #1): only the cart's own owner (or admin) may clear it.
        SecurityUtils.requireSelfOrAdmin(userId);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setCartId(cart.getCartId());
        dto.setCreatedAt(cart.getCreatedAt());

        if (cart.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(cart.getUser().getUserId());
            userDTO.setName(cart.getUser().getName());
            userDTO.setEmail(cart.getUser().getEmail());
            userDTO.setRole(cart.getUser().getRole().name());
            dto.setUser(userDTO);
        }

        if (cart.getCartItems() != null) {
            List<CartItemDTO> itemDTOs = cart.getCartItems().stream()
                    .map(this::convertCartItemToDTO)
                    .collect(Collectors.toList());
            dto.setCartItems(itemDTOs);
        }

        return dto;
    }

    private CartItemDTO convertCartItemToDTO(CartItem cartItem) {
        CartItemDTO dto = new CartItemDTO();
        dto.setCartItemId(cartItem.getCartItemId());
        dto.setQuantity(cartItem.getQuantity());

        if (cartItem.getProduct() != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(cartItem.getProduct().getProductId());
            productDTO.setName(cartItem.getProduct().getName());
            productDTO.setBrand(cartItem.getProduct().getBrand());
            productDTO.setPrice(cartItem.getProduct().getPrice());
            productDTO.setStockQty(cartItem.getProduct().getStockQty());
            dto.setProduct(productDTO);
        }

        return dto;
    }
}