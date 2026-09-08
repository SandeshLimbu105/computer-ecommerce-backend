package org.texas.computerecommerce.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long paymentId;
    private String txnRef;
    private String status;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
}
