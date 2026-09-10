package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Cart;
import org.texas.computerecommerce.Entity.CartItem;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Repository.CartItemRepository;
import org.texas.computerecommerce.Repository.CartRepository;
import org.texas.computerecommerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));
    }

    @Transactional
    public Cart addItemToCart(Long userId, Long productId, int quantity) {
        System.out.println("=== Adding item to cart ===");
        System.out.println("User ID: " + userId);
        System.out.println("Product ID: " + productId);
        System.out.println("Quantity: " + quantity);

        // Get user's cart
        Cart cart = getCartByUserId(userId);
        System.out.println("Cart found: " + cart.getCartId());

        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    System.err.println("Product not found: " + productId);
                    return new RuntimeException("Product not found: " + productId);
                });
        System.out.println("Product found: " + product.getName());
        System.out.println("Stock available: " + product.getStockQty());

        // Check stock
        if (product.getStockQty() < quantity) {
            System.err.println("Insufficient stock! Available: " + product.getStockQty() + ", Requested: " + quantity);
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }

        // Check if product already in cart
        CartItem existingItem = cartItemRepository.findByCart_CartIdAndProduct_ProductId(
                cart.getCartId(), productId);

        if (existingItem != null) {
            // Update quantity
            int newQuantity = existingItem.getQuantity() + quantity;
            System.out.println("Updating existing item from " + existingItem.getQuantity() + " to " + newQuantity);
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Add new item
            System.out.println("Adding new item to cart");
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            cart.getCartItems().add(cartItem);
        }

        Cart updatedCart = cartRepository.save(cart);
        System.out.println("Cart updated successfully!");
        return updatedCart;
    }
@Transactional
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
@Transactional
    public Cart removeItemFromCart(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found: " + cartItemId));

        Long cartId = cartItem.getCart().getCartId();
        cartItemRepository.deleteById(cartItemId);

        return cartRepository.findById(cartId).orElseThrow();
    }
@Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartByUserId(userId);
        cartItemRepository.deleteByCart_CartId(cart.getCartId());
    }
}