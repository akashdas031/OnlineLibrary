package PaymentService.DTOs;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String productId;
    private double amount; // Amount in INR
}