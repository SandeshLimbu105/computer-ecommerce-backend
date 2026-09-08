package org.texas.computerecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.texas.computerecommerce.Entity.Payment;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrder_OrderId(Long orderId);

    // Find payment by transaction reference - USED IN PaymentService
    Optional<Payment> findByTxnRef(String txnRef);

    // Check if payment exists by order ID - USED IN PaymentService
    boolean existsByOrder_OrderId(Long orderId);

    // Delete payment by order ID
    void deleteByOrder_OrderId(Long orderId);
}
