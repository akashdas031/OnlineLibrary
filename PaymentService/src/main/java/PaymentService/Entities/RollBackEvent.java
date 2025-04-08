package PaymentService.Entities;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RollBackEvent {
    
	private Order order;
	private String serviceName;
	private String reason;
	private LocalDateTime time;
}

