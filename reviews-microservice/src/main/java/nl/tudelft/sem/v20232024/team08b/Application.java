package nl.tudelft.sem.v20232024.team08b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Example microservice application.
 */
@SpringBootApplication(
        exclude = {
                SecurityAutoConfiguration.class,
                //ManagementWebSecurityAutoConfiguration.class
        })
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}