package PaymentService.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Payment {
    @Id
    private String id;
    private String transactionId;
    private String orderId;
    private String paymentId;
    private String status;
    private String userId;
    private double amount;
}