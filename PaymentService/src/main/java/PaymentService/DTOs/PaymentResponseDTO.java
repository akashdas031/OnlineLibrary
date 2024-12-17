package PaymentService.DTOs;

import lombok.Data;

@Data
public class PaymentResponseDTO {
    private String orderId;
    private String paymentLink;
    private String status;
    private String paymentId;
}

