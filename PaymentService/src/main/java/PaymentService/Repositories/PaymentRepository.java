package PaymentService.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import PaymentService.Entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String>{

	Optional<Payment> findByOrderId(String orderId);
	Optional<Payment> findByPaymentId(String paymentId);
}
