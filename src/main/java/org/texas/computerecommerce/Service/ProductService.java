package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Category;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Repository.CategoryRepository;
import org.texas.computerecommerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingOrBrandContaining(keyword, keyword);
    }

    public Product createProduct(Product product, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
        product.setCategory(category);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        product.setName(productDetails.getName());
        product.setBrand(productDetails.getBrand());
        product.setDescription(productDetails.getDescription());
        product.setImageUrl(productDetails.getImageUrl());
        product.setSpecJson(productDetails.getSpecJson());
        product.setPrice(productDetails.getPrice());
        product.setStockQty(productDetails.getStockQty());
        product.setIsActive(productDetails.getIsActive());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public void updateStock(Long productId, int stockQty) {
        Product product = getProductById(productId);
        product.setStockQty(stockQty);
        productRepository.save(product);
    }

    public boolean isProductInStock(Long productId, int requestedQuantity) {
        Product product = getProductById(productId);
        return product.getStockQty() >= requestedQuantity;
    }

    public void deductStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        if (product.getStockQty() < quantity) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        product.setStockQty(product.getStockQty() - quantity);
        productRepository.save(product);
    }
}