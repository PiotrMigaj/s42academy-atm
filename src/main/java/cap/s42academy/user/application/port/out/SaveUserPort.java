package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;

@FunctionalInterface
public interface SaveUserPort {
    UserId save(User user);
}
