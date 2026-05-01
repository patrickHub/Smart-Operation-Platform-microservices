package ch.smart.operations.platform.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ch.smart.operations.platform")
@EnableJpaRepositories(basePackages = {"ch.smart.operations.platform.customer.infrastructure.persistence.repositories", "ch.smart.operations.platform.asset.infrastructure.persistence.repositories"})
@EntityScan(basePackages = {"ch.smart.operations.platform.customer.infrastructure.persistence.entities", "ch.smart.operations.platform.asset.infrastructure.persistence.entities"})
public class SmartOpPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartOpPlatformApplication.class, args);
	}

}
