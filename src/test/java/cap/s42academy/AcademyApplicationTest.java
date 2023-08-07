package cap.s42academy;

import cap.s42academy.configuration.AcademyApplicationTestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AcademyApplicationTestConfiguration.class)
@DataJpaTest
class AcademyApplicationTest {

}
