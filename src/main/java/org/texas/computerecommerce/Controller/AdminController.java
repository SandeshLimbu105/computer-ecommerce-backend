package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.OrderDTO;
import org.texas.computerecommerce.Dto.ProductDTO;
import org.texas.computerecommerce.Entity.Order;
import org.texas.computerecommerce.Entity.Product;
import org.texas.computerecommerce.Service.OrderService;
import org.texas.computerecommerce.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderController orderController;

    // ✅ FIX (bug #3): Inject ProductController via Spring instead of `new`
    @Autowired
    private ProductController productController;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> dtos = orders.stream()
                .map(orderController::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(orderController.convertToDTO(order));
    }

    @PutMapping("/products/{productId}/stock")
    public ResponseEntity<ProductDTO> updateStock(
            @PathVariable Long productId,
            @RequestBody Map<String, Integer> request) {
        int stockQty = request.get("stockQty");
        productService.updateStock(productId, stockQty);
        Product product = productService.getProductById(productId);

        // ✅ FIX: Use the injected bean instead of `new ProductController()`
        return ResponseEntity.ok(productController.convertToDTO(product));
    }
}