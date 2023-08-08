package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.valueobject.UserId;

@FunctionalInterface
public interface ExistsOpenSessionForUserWithIdPort {
    boolean existsOpenSession(UserId userId);
}
