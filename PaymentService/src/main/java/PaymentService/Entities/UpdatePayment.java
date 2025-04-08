package PaymentService.Entities;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePayment {

    String paymentId;
    String orderId;
    String status;
}
