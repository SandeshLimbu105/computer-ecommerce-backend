package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.*;
import org.texas.computerecommerce.Entity.Order;
import org.texas.computerecommerce.Entity.OrderItem;
import org.texas.computerecommerce.Security.SecurityUtils;
import org.texas.computerecommerce.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<OrderDTO> placeOrder(
            @RequestParam Long userId,
            @RequestBody Map<String, String> request) {
        // ✅ FIX (bug #2): a customer may only place an order for themselves.
        SecurityUtils.requireSelfOrAdmin(userId);
        String shippingAddress = request.get("shippingAddress");
        Order order = orderService.placeOrder(userId, shippingAddress);
        return new ResponseEntity<>(convertToDTO(order), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        // ✅ FIX (bug #2): only the user (or admin) may view their order list.
        SecurityUtils.requireSelfOrAdmin(userId);
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        // ✅ FIX (bug #2): only the order's owner (or admin) may view it.
        SecurityUtils.requireSelfOrAdmin(order.getUser().getUserId());
        return ResponseEntity.ok(convertToDTO(order));
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        // ✅ FIX (bug #3): admin-only. Also enforced in SecurityConfig.
        SecurityUtils.requireSelfOrAdmin(null);
        List<Order> orders = orderService.getAllOrders();
        List<OrderDTO> dtos = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> request) {
        // ✅ FIX: This endpoint is admin-only (defense-in-depth).
        SecurityUtils.requireSelfOrAdmin(null);
        String status = request.get("status");
        Order order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(convertToDTO(order));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long orderId) {
        Order existing = orderService.getOrderById(orderId);
        // ✅ FIX (bug #2): only the order's owner (or admin) may cancel it.
        SecurityUtils.requireSelfOrAdmin(existing.getUser().getUserId());
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(convertToDTO(order));
    }

    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());

        if (order.getUser() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(order.getUser().getUserId());
            userDTO.setName(order.getUser().getName());
            userDTO.setEmail(order.getUser().getEmail());
            userDTO.setRole(order.getUser().getRole().name());
            dto.setUser(userDTO);
        }

        if (order.getOrderItems() != null) {
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(this::convertOrderItemToDTO)
                    .collect(Collectors.toList());
            dto.setOrderItems(itemDTOs);
        }

        if (order.getPayment() != null) {
            PaymentDTO paymentDTO = new PaymentDTO();
            paymentDTO.setPaymentId(order.getPayment().getPaymentId());
            paymentDTO.setTxnRef(order.getPayment().getTxnRef());
            paymentDTO.setStatus(order.getPayment().getStatus());
            paymentDTO.setAmount(order.getPayment().getAmount());
            paymentDTO.setPaymentDate(order.getPayment().getPaymentDate());
            dto.setPayment(paymentDTO);
        }

        return dto;
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setOrderItemId(orderItem.getOrderItemId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setPriceAtPurchase(orderItem.getPriceAtPurchase());

        if (orderItem.getProduct() != null) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(orderItem.getProduct().getProductId());
            productDTO.setName(orderItem.getProduct().getName());
            productDTO.setBrand(orderItem.getProduct().getBrand());
            productDTO.setPrice(orderItem.getProduct().getPrice());
            dto.setProduct(productDTO);
        }

        return dto;
    }
}