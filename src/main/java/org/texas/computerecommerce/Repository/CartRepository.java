package org.texas.computerecommerce.Repository;

import org.texas.computerecommerce.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    // ✅ CORRECT: Returns Optional<Cart>
    Optional<Cart> findByUser_UserId(Long userId);

    // ✅ Optional: Check if cart exists
    boolean existsByUser_UserId(Long userId);

    // ✅ Optional: Delete by user ID
    void deleteByUser_UserId(Long userId);
}