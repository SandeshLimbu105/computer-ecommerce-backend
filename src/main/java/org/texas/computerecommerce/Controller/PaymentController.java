package org.texas.computerecommerce.Controller;

import org.texas.computerecommerce.Dto.PaymentDTO;
import org.texas.computerecommerce.Entity.Payment;
import org.texas.computerecommerce.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<PaymentDTO> initiatePayment(@PathVariable Long orderId) {
        Payment payment = paymentService.initiatePayment(orderId);
        return new ResponseEntity<>(convertToDTO(payment), HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentDTO> getPaymentByOrder(@PathVariable Long orderId) {
        Payment payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(convertToDTO(payment));
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, String> callback) {
        String status = callback.get("status");
        String txnRef = callback.get("txnRef");

        if (status == null || txnRef == null) {
            return ResponseEntity.badRequest().body("Missing status or txnRef");
        }

        paymentService.handlePaymentCallback(status, txnRef);
        return ResponseEntity.ok("Payment callback processed successfully.");
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setTxnRef(payment.getTxnRef());
        dto.setStatus(payment.getStatus());
        dto.setAmount(payment.getAmount());
        dto.setPaymentDate(payment.getPaymentDate());
        return dto;
    }
}