package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Cart;
import org.texas.computerecommerce.Entity.CartItem;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Repository.CartItemRepository;
import org.texas.computerecommerce.Repository.CartRepository;
import org.texas.computerecommerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUserId(Long userId) {
        return (Cart) cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCartByUserId(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        // Check if product already in cart
        CartItem existingItem = cartItemRepository.findByCart_CartIdAndProduct_ProductId(
                cart.getCartId(), productId);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        return cartRepository.save(cart);
    }

    public Cart updateCartItem(Long cartItemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        if (quantity <= 0) {
            return removeItemFromCart(cartItemId);
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return cartRepository.findById(cartItem.getCart().getCartId()).orElseThrow();
    }

    public Cart removeItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        Long cartId = cartItem.getCart().getCartId();
        cartItemRepository.deleteById(cartItemId);

        return cartRepository.findById(cartId).orElseThrow();
    }

    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteByCart_CartId(cart.getCartId());
    }
}