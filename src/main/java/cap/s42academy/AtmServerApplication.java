package cap.s42academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "cap.s42academy")
@EntityScan(basePackages = "cap.s42academy")
@EnableJpaRepositories(basePackages = "cap.s42academy")
@ConfigurationPropertiesScan("cap.s42academy")
public class AtmServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtmServerApplication.class, args);
	}

}
