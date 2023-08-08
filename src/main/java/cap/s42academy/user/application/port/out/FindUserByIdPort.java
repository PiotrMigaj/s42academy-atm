package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;

import java.util.Optional;

@FunctionalInterface
public interface FindUserByIdPort {
    Optional<User> findBy(UserId userId);
}
