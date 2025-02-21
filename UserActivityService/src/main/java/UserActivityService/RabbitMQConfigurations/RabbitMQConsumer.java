package UserActivityService.RabbitMQConfigurations;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import UserActivityService.Entities.UserActivity;
import UserActivityService.Repositories.UserActivityRepository;
import lombok.RequiredArgsConstructor;

@Service
public class RabbitMQConsumer {

	private Logger logger=LoggerFactory.getLogger(RabbitMQConsumer.class);
	private UserActivityRepository userActivityRepo;
	public RabbitMQConsumer(UserActivityRepository userActivityRepo) {
		this.userActivityRepo=userActivityRepo;
	}
	
	@RabbitListener(queues="user_activity_queue")
	public void receiveMessage(UserActivity activity) {
		
		//UserActivity activity=objectMapper.readValue(message, UserActivity.class);
		logger.info("message from rabbit server :"+activity);
		UserActivity userActivity = this.userActivityRepo.save(activity);
		logger.info("UserActivity from db :"+userActivity);
		
	}
}
