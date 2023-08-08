package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.valueobject.UserId;

import java.util.Optional;

@FunctionalInterface
public interface GetOpenSessionForUserWithIdPort {
    Optional<Session> getOpenSession(UserId userId);
}
