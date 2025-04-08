package PaymentService.Entities;

import java.io.Serializable;
import java.time.LocalDateTime;

import PaymentService.Enums.OrderStatus;
import PaymentService.Enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Order implements Serializable{
	
	private String orderId;
	private String productId;
	private String quantity;
	private String price;
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;
	private LocalDateTime orderPlacedTime;
	private LocalDateTime orderUpdatedTime;
}

