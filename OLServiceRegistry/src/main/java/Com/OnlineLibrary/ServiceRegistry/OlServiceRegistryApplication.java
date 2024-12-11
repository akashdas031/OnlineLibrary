package Com.OnlineLibrary.ServiceRegistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class OlServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlServiceRegistryApplication.class, args);
	}

}
