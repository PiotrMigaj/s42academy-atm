package cap.s42academy.user.adapter.out;

import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.SessionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static cap.s42academy.SampleTestDataFactory.session;
import static cap.s42academy.SampleTestDataFactory.user;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void shouldReturnFalse_whenThereInNoOpenSession(){
        //given
        User user = user();
        testEntityManager.persist(user);
        //when
        boolean result = sessionRepository.existsOpenSessionForUser(user.getUserId());
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalse_whenThereIsClosedSession(){
        //given
        User user = user();
        Session session = session(SessionStatus.CLOSED, LocalDateTime.now(), LocalDateTime.now(), user);
        testEntityManager.persist(user);
        testEntityManager.persist(session);
        //when
        boolean result = sessionRepository.existsOpenSessionForUser(user.getUserId());
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrue_whenThereIsOpenSession(){
        //given
        User user = user();
        Session session = session(SessionStatus.OPEN, LocalDateTime.now(), null, user);
        testEntityManager.persist(user);
        testEntityManager.persist(session);
        //when
        boolean result = sessionRepository.existsOpenSessionForUser(user.getUserId());
        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnEmptyOptional_whenThereInNoOpenSession(){
        //given
        User user = user();
        testEntityManager.persist(user);
        //when
        Optional<Session> result = sessionRepository.findOpenSessionForUser(user.getUserId());
        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyOptional_whenThereIsClosedSession(){
        //given
        User user = user();
        Session session = session(SessionStatus.CLOSED, LocalDateTime.now(), LocalDateTime.now(), user);
        testEntityManager.persist(user);
        testEntityManager.persist(session);
        //when
        Optional<Session> result = sessionRepository.findOpenSessionForUser(user.getUserId());
        //then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnSession_whenThereIsOpenSession(){
        //given
        User user = user();
        Session session = session(SessionStatus.OPEN, LocalDateTime.now(), null, user);
        testEntityManager.persist(user);
        testEntityManager.persist(session);
        //when
        Optional<Session> result = sessionRepository.findOpenSessionForUser(user.getUserId());
        //then
        assertThat(result).isNotEmpty();
    }

}
