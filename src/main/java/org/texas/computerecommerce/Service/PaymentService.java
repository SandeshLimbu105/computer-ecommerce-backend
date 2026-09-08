package org.texas.computerecommerce.Service;

import org.texas.computerecommerce.Entity.Order;
import org.texas.computerecommerce.Entity.Payment;
import org.texas.computerecommerce.Repository.OrderRepository;
import org.texas.computerecommerce.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderRepository orderRepository;

    public Payment getPaymentByOrderId(Long orderId) {
        return (Payment) paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
    }

    @Transactional
    public Payment initiatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new RuntimeException("Order is not pending payment. Current status: " + order.getStatus());
        }

        // Check if payment already exists
        if (paymentRepository.findByOrder_OrderId(orderId).isPresent()) {
            throw new RuntimeException("Payment already initiated for this order.");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("PENDING");
        payment.setPaymentDate(LocalDateTime.now());
        payment.setTxnRef("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        return paymentRepository.save(payment);
    }

    @Transactional
    public void handlePaymentCallback(String status, String txnRef) {
        Payment payment = (Payment) paymentRepository.findByTxnRef(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found with reference: " + txnRef));

        payment.setStatus(status);
        paymentRepository.save(payment);

        // Update order status
        Order order = payment.getOrder();
        if ("SUCCESS".equalsIgnoreCase(status)) {
            order.setStatus("CONFIRMED");
        } else if ("FAILED".equalsIgnoreCase(status)) {
            order.setStatus("FAILED");
        }
        orderRepository.save(order);
    }
}