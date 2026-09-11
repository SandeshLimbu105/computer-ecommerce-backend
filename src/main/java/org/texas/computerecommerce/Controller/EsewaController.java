package org.texas.computerecommerce.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.texas.computerecommerce.Service.EsewaService;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/esewa")
public class EsewaController {

    private final EsewaService esewaService;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    public EsewaController(EsewaService esewaService) {
        this.esewaService = esewaService;
    }

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long orderId) {
        Map<String, String> response = esewaService.initiatePayment(orderId);
        return ResponseEntity.ok(response);
    }

    // ✅ FIX (bug #7): pass orderId to frontend for accurate purchase tracking
    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam String data) {
        String status = "success";
        Long orderId = null;
        try {
            orderId = esewaService.verifyPaymentCallback(data).getOrderId();
        } catch (Exception e) {
            status = "failure";
        }
        String redirect = frontendBaseUrl + "/payment-result?status=" + status
                + (orderId != null ? "&orderId=" + orderId : "");
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirect))
                .build();
    }

    @GetMapping("/failure")
    public ResponseEntity<Void> failure(
            @RequestParam(value = "data", required = false) String data) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendBaseUrl + "/payment-result?status=failure"))
                .build();
    }
}