package cap.s42academy.user.application.port.in;

import cap.s42academy.user.domain.valueobject.UserId;

@FunctionalInterface
public interface ExistsUserByIdUseCase {

    boolean existsBy(UserId userId);

}
