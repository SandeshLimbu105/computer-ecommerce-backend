package org.texas.computerecommerce.Controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.texas.computerecommerce.Service.EsewaService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments/esewa")
@AllArgsConstructor
public class EsewaController {

    private final EsewaService esewaService;
    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<Map<String, String>> initiatePayment(@PathVariable Long orderId) {
       Map<String, String> response = esewaService.initiatePayment(orderId);
       return ResponseEntity.ok(response);
    }
    @GetMapping("/success")
    public ResponseEntity<String> success(@RequestParam String data) {
        String result=esewaService.verifyPaymentCallback(data);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/failure")
    public ResponseEntity<String> failure(@RequestParam String data) {
        return ResponseEntity.ok("Payment failed or cancelled");
    }

}
