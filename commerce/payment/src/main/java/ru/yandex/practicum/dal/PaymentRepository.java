package ru.yandex.practicum.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.dto.payment.PaymentState;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByOrderIdOptional(String orderId);

    Payment findByOrderId(String orderId);

    // METHODS FOR POLLERS

    Optional<Payment> findFirstByPaymentStateOrderByTouchedAtAsc(PaymentState state);

}