package org.texas.computerecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.texas.computerecommerce.Entity.Product;

import java.util.List;

@Repository

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Find products by category ID
    List<Product> findByCategory_CategoryId(Long categoryId);

    // Search products by name OR brand
    List<Product> findByNameContainingOrBrandContaining(String nameKeyword, String brandKeyword);
}