package ch.smart.operations.platform.workorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "ch.smart.operations.platform")
@EnableJpaRepositories(basePackages = {
	"ch.smart.operations.platform.workorder.infrastructure.persistence.repositories",
})
@EntityScan(basePackages = {
	"ch.smart.operations.platform.workorder.infrastructure.persistence.entities",
})
public class SmartOpWorkOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartOpWorkOrderApplication.class, args);
	}

}
