package ch.smart.operations.platform.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SmartOpCustomerConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
    /*
        Sometime it happpens that Spring Boot is not auto-configuring Flyway at all, even though the dependency and properties exist.
        then in such case add explicit Flyway migration configuration
    */
    @Bean
    public Flyway customerFlyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .schemas("customer")
                .defaultSchema("customer")
                .createSchemas(true)
                .table("flyway_schema_history")
                .load();
    }

    @Bean
    public FlywayMigrationInitializer customerFlywayMigrationInitializer(Flyway customerFlyway) {
        return new FlywayMigrationInitializer(customerFlyway);
    }
}