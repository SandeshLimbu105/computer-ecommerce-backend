// RecommendationService.java
package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Order;
import org.texas.computerecommerce.Entity.OrderItem;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Repository.OrderRepository;
import org.texas.computerecommerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // 1. People who bought this product also bought...
    public List<Product> getFrequentlyBoughtTogether(Long productId) {
        // Find all orders containing this product
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> order.getOrderItems().stream()
                        .anyMatch(item -> item.getProduct().getProductId().equals(productId)))
                .collect(Collectors.toList());

        // Collect all products from these orders
        Map<Long, Integer> productFrequency = new HashMap<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Long id = item.getProduct().getProductId();
                if (!id.equals(productId)) {
                    productFrequency.put(id, productFrequency.getOrDefault(id, 0) + 1);
                }
            }
        }

        // Sort by frequency and get top products
        List<Long> recommendedIds = productFrequency.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return productRepository.findAllById(recommendedIds);
    }

    // 2. Personalized recommendations based on user's purchase history
    public List<Product> getPersonalizedRecommendations(Long userId) {
        // Get user's order history
        List<Order> userOrders = orderRepository.findByUser_UserId(userId);

        // Get categories user bought from
        Set<Long> categoryIds = userOrders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .map(item -> item.getProduct().getCategory().getCategoryId())
                .collect(Collectors.toSet());

        // Recommend products from same categories (excluding products already bought)
        List<Product> recommendations = productRepository.findAll().stream()
                .filter(product -> categoryIds.contains(product.getCategory().getCategoryId()))
                .filter(product -> !hasUserBoughtProduct(userId, product.getProductId()))
                .limit(10)
                .collect(Collectors.toList());

        return recommendations;
    }

    private boolean hasUserBoughtProduct(Long userId, Long productId) {
        List<Order> orders = orderRepository.findByUser_UserId(userId);
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getProductId().equals(productId));
    }

    // 3. Trending products
    public List<Product> getTrendingProducts() {
        List<Order> allOrders = orderRepository.findAll();

        Map<Long, Integer> productSales = new HashMap<>();
        for (Order order : allOrders) {
            for (OrderItem item : order.getOrderItems()) {
                Long id = item.getProduct().getProductId();
                productSales.put(id, productSales.getOrDefault(id, 0) + item.getQuantity());
            }
        }

        List<Long> trendingIds = productSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return productRepository.findAllById(trendingIds);
    }
}