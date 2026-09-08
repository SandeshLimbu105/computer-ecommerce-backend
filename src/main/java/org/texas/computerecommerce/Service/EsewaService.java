package org.texas.computerecommerce.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.texas.computerecommerce.Entity.Order;
import org.texas.computerecommerce.Entity.Payment;
import org.texas.computerecommerce.Repository.OrderRepository;
import org.texas.computerecommerce.Repository.PaymentRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class EsewaService {

    @Value("${esewa.sandbox.url}")
    private String sandboxUrl;

    @Value("${esewa.merchant.code}")
    private String merchantCode;

    @Value("${esewa.secret.key}")
    private String secretKey;

    @Value("${esewa.success.url}")
    private String successUrl;

    @Value("${esewa.failure.url}")
    private String failureUrl;

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EsewaService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("=== ESEWA CONFIG LOADED ===");
        System.out.println("sandboxUrl: " + sandboxUrl);
        System.out.println("merchantCode: " + merchantCode);
        System.out.println("secretKey: " + secretKey);
        System.out.println("successUrl: " + successUrl);
        System.out.println("failureUrl: " + failureUrl);
    }

    public Map<String, String> initiatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (paymentRepository.findByOrder_OrderId(orderId).isPresent()) {
            throw new RuntimeException("Payment already initiated for this order!");
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus("PENDING");
        String txnUuid = UUID.randomUUID().toString();
        payment.setTxnRef(txnUuid);
        paymentRepository.save(payment);

        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("amount", String.valueOf(order.getTotalAmount()));
        paymentData.put("tax_amount", "0");
        paymentData.put("product_service_charge", "0");
        paymentData.put("product_delivery_charge", "0");
        paymentData.put("transaction_uuid", txnUuid);
        paymentData.put("product_code", merchantCode);
        paymentData.put("success_url", successUrl);
        paymentData.put("failure_url", failureUrl);
        paymentData.put("signed_field_names", "total_amount,transaction_uuid,product_code");

        String signature = generateSignature(paymentData);
        paymentData.put("signature", signature);

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", sandboxUrl);
        response.put("txnRef", txnUuid);
        response.put("amount", String.valueOf(order.getTotalAmount()));

        return response;
    }

    private String generateSignature(Map<String, String> data) {
        try {
            // Log the secret key to verify it's not null
            System.out.println("Secret Key in generateSignature: " + secretKey);

            String signedFieldNames = data.get("signed_field_names");
            String[] fields = signedFieldNames.split(",");

            StringBuilder message = new StringBuilder();
            for (String field : fields) {
                String value = data.get(field.trim());
                if (value != null) {
                    message.append(field.trim()).append("=").append(value);
                    if (!field.equals(fields[fields.length - 1])) {
                        message.append(",");
                    }
                }
            }

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    secretKey.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            byte[] signatureBytes = mac.doFinal(message.toString().getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(signatureBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate signature: " + e.getMessage());
        }
    }

    public String verifyPaymentCallback(String encodedData) {
        try {
            String decoded = new String(Base64.getDecoder().decode(encodedData));
            System.out.println("Decoded: " + decoded);

            @SuppressWarnings("unchecked")
            Map<String, String> response = objectMapper.readValue(decoded, Map.class);

            String receivedSignature = response.get("signature");
            String generatedSignature = generateSignature(response);

            if (!generatedSignature.equals(receivedSignature)) {
                throw new RuntimeException("Invalid signature!");
            }

            String status = response.get("status");
            String txnRef = response.get("transaction_uuid");

            Payment payment = paymentRepository.findByTxnRef(txnRef)
                    .orElseThrow(() -> new RuntimeException("Payment not found: " + txnRef));

            if ("COMPLETE".equalsIgnoreCase(status)) {
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);

                Order order = payment.getOrder();
                order.setStatus("CONFIRMED");
                orderRepository.save(order);

                return "Payment successful!";
            } else {
                payment.setStatus("FAILED");
                paymentRepository.save(payment);

                Order order = payment.getOrder();
                order.setStatus("FAILED");
                orderRepository.save(order);

                return "Payment failed!";
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify payment: " + e.getMessage());
        }
    }
}