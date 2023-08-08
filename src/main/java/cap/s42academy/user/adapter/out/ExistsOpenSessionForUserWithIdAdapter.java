package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.ExistsOpenSessionForUserWithIdPort;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExistsOpenSessionForUserWithIdAdapter implements ExistsOpenSessionForUserWithIdPort {

    private final SessionRepository sessionRepository;
    @Override
    public boolean existsOpenSession(UserId userId) {
        return sessionRepository.existsOpenSessionForUser(userId);
    }
}
