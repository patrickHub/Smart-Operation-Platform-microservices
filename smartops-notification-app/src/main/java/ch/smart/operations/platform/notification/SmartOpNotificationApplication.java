package ch.smart.operations.platform.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableKafka
@SpringBootApplication(scanBasePackages = "ch.smart.operations.platform")
@EnableJpaRepositories(basePackages = {
	"ch.smart.operations.platform.notification.infrastructure.persistence.repositories",
})
@EntityScan(basePackages = {
	"ch.smart.operations.platform.notification.infrastructure.persistence.entities",
})
public class SmartOpNotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartOpNotificationApplication.class, args);
	}

}
