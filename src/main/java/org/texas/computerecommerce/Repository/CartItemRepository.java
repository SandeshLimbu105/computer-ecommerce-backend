package org.texas.computerecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.texas.computerecommerce.Entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByCart_CartIdAndProduct_ProductId(Long cartId, Long productId);

    void deleteByCart_CartId(Long cartId);
}
