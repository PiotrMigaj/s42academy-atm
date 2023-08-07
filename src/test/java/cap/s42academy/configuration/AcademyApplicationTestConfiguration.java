package cap.s42academy.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ComponentScan(basePackages = { "cap.s42academy" })
@EntityScan(basePackages = { "cap.s42academy" })
@EnableJpaRepositories(basePackages = { "cap.s42academy" })
public class AcademyApplicationTestConfiguration {

}
