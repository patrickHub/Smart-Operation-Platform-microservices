package ch.smart.operations.platform.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "ch.smart.operations.platform")
@EnableJpaRepositories(basePackages = {
	"ch.smart.operations.platform.billing.infrastructure.persistence.repositories",
})
@EntityScan(basePackages = {
	"ch.smart.operations.platform.billing.infrastructure.persistence.entities",
})
public class SmartOpBillingApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartOpBillingApplication.class, args);
	}

}
