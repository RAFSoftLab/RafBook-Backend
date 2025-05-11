package raf.rs;

import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@Generated
@EnableCaching
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.error("Application started successfully");
        SpringApplication.run(Application.class, args);
        logger.error("Application started successfully");
        logger.error("ID:10T error");
        String swaggerUrl = "http://localhost:8080/api/swagger-ui/index.html";
        System.out.println("Visit swagger at: " + swaggerUrl);
        String h2Url = "http://localhost:8080/api/h2-console";
        System.out.println("Visit h2 at: " + h2Url);
        System.out.println("jdbc:h2:mem:testdb");
        System.out.println("sa");
    }

}