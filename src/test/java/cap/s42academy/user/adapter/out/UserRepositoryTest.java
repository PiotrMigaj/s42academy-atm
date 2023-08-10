package cap.s42academy.user.adapter.out;

import cap.s42academy.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.user;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void shouldReturnTrue_whenExistsByEmail(){
        //given
        User user = user();
        testEntityManager.persist(user);
        //when
        boolean result = userRepository.existsByEmail(user.getEmail());
        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenDoesNotExistsByEmail(){
        //given
        User user = user();
        testEntityManager.persist(user);
        //when
        boolean result = userRepository.existsByEmail(UUID.randomUUID().toString());
        //then
        assertThat(result).isFalse();
    }

}
