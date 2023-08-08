package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.GetOpenSessionForUserWithIdPort;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class GetOpenSessionForUserWithIdAdapter implements GetOpenSessionForUserWithIdPort {

    private final SessionRepository sessionRepository;
    @Override
    public Optional<Session> getOpenSession(UserId userId) {
        return sessionRepository.findOpenSessionForUser(userId);
    }
}
