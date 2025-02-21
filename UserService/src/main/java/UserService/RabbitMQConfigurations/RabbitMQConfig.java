package UserService.RabbitMQConfigurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

	public static final String USER_ACTIVITY_QUEUE="user_activity_queue";
	private static final String EXCHANGE_NAME="user_activity_exchange";
	private static final String ROUTING_KEY="user_activity";
	
	@Bean
	public Queue userActivityQueue() {
		return new Queue(USER_ACTIVITY_QUEUE,true);
	}
	
	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE_NAME);
	}
	
	@Bean
	public Binding binding(Queue queue,DirectExchange directExchange) {
		return BindingBuilder.bind(queue).to(directExchange).with(ROUTING_KEY);
	}
	@Bean
	public Jackson2JsonMessageConverter jsonMessageConvertor() {
		return new Jackson2JsonMessageConverter();
	}
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,Jackson2JsonMessageConverter convertor) {
		RabbitTemplate rabbitTemplate=new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(convertor);
		return rabbitTemplate;
	}
}
