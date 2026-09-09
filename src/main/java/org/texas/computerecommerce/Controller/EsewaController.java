package org.texas.computerecommerce.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.texas.computerecommerce.Service.EsewaService;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/esewa")
// ❌ Remove @AllArgsConstructor
public class EsewaController {

    private final EsewaService esewaService;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    // ✅ Create constructor manually
    public EsewaController(EsewaService esewaService) {
        this.esewaService = esewaService;
    }

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long orderId) {
        Map<String, String> response = esewaService.initiatePayment(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<Void> success(@RequestParam String data) {
        String status = "success";
        try {
            esewaService.verifyPaymentCallback(data);
        } catch (Exception e) {
            status = "failure";
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendBaseUrl + "/payment-result?status=" + status))
                .build();
    }

    @GetMapping("/failure")
    public ResponseEntity<Void> failure(@RequestParam String data) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendBaseUrl + "/payment-result?status=failure"))
                .build();
    }
}