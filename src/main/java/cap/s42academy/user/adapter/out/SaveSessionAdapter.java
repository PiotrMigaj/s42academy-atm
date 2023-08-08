package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.SaveSessionPort;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.valueobject.SessionId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class SaveSessionAdapter implements SaveSessionPort {

    private final SessionRepository sessionRepository;


    @Override
    public SessionId save(Session session) {
        return sessionRepository.save(session).getSessionId();
    }
}
