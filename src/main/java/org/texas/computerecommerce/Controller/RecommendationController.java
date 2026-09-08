// RecommendationController.java
package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.ProductDTO;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.texas.computerecommerce.Controller.ProductController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private ProductController productController;

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductDTO>> getFrequentlyBoughtTogether(@PathVariable Long productId) {
        List<Product> products = recommendationService.getFrequentlyBoughtTogether(productId);
        List<ProductDTO> dtos = products.stream()
                .map(productController::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductDTO>> getPersonalizedRecommendations(@PathVariable Long userId) {
        List<Product> products = recommendationService.getPersonalizedRecommendations(userId);
        List<ProductDTO> dtos = products.stream()
                .map(productController::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<ProductDTO>> getTrendingProducts() {
        List<Product> products = recommendationService.getTrendingProducts();
        List<ProductDTO> dtos = products.stream()
                .map(productController::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}