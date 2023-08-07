package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.entity.User;

import java.util.UUID;

@FunctionalInterface
public interface SaveUserPort {
    UUID saveUser(User user);
}
