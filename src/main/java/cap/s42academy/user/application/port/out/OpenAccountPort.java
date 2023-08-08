package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.valueobject.UserId;

@FunctionalInterface
public interface OpenAccountPort {
    void openAccountForUser(UserId userId);
}
