package PaymentService.DTOs;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private String userId;
    private double amount; // Amount in INR
}