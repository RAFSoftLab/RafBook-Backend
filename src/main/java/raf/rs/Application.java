package raf.rs;

import lombok.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@Generated
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        String swaggerUrl = "http://localhost:8080/api/swagger-ui/index.html";
        System.out.println("Visit swagger at: " + swaggerUrl);
        String h2Url = "http://localhost:8080/api/h2-console";
        System.out.println("Visit h2 at: " + h2Url);
        System.out.println("jdbc:h2:mem:testdb");
        System.out.println("sa");
    }

}
