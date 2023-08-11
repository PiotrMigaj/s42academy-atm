package cap.s42academy.user.adapter.out;

import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

interface SessionRepository extends JpaRepository<Session, SessionId> {


    @Query("""
            select s from Session s 
            join s.user u
            where u.userId = :userId
            and s.sessionStatus = cap.s42academy.user.domain.valueobject.SessionStatus.OPEN 
            """)
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Session> findOpenSessionForUser(@Param("userId") UserId userId);

    @Query("""
            select count (s.sessionId) > 0 from Session s 
            join s.user u
            where u.userId = :userId
            and s.sessionStatus = cap.s42academy.user.domain.valueobject.SessionStatus.OPEN 
            """)
//    @Lock(LockModeType.PESSIMISTIC_READ)
//    TODO: Issue with H2 database -> does not work on H2
    boolean existsOpenSessionForUser(@Param("userId") UserId userId);
}
