package UserService.RabbitMQConfigurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import UserService.DTOs.UserActivityDTO;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserActivityProducer {

	private Logger logger=LoggerFactory.getLogger(UserActivityProducer.class);
	private final RabbitTemplate rabbitTemplate;
	private final String EXCHANGE_NAME="user_activity_exchange";
	private final String ROUTING_KEY="user_activity";
	
	public void sendActivity(UserActivityDTO userActivity) {
		logger.info("user activity : "+userActivity);
		this.rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY,userActivity);
		
	}
}
