package springbootrest;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final CustomerRepository repository;

    @Autowired
    public Application(CustomerRepository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        try (var reader = new BufferedReader(
                new InputStreamReader(
                        Application.class.getResourceAsStream("/data.csv")
                ))) {
            var customers = new ArrayList<Customer>();
            String COMMA = ",";
            reader.lines().forEach(s -> {
                var split = s.split(COMMA);
                var firstName = split[0];
                var lastName = split[1];
                var email = split[2];
                var avatar = split[3];
                customers.add(new Customer(firstName, lastName, email, avatar));
            });
            this.repository.insert(customers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${application-description}") String appDescription,
                                 @Value("${application-version}") String appVersion,
                                 @Value("${application-name}") String appName) {
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version(appVersion)
                        .description(appDescription)
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact().name("erikmolssons").url("https://github.com/erikmolssons"))
                        .license(new License().name("MIT")));
    }


    @PreDestroy
    public void destroy() {
        this.repository.deleteAll();
    }
}
