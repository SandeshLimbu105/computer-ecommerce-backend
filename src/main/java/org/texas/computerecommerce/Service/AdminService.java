package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Order;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Repository.OrderRepository;
import org.texas.computerecommerce.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Product updateProductStock(Long productId, int stockQty) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));
        product.setStockQty(stockQty);
        return productRepository.save(product);
    }
}