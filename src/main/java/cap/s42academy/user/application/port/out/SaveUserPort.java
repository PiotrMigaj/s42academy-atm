package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;

import java.util.UUID;

@FunctionalInterface
public interface SaveUserPort {
    UserId saveUser(User user);
}
