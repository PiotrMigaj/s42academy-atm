package cap.s42academy.user.application.port.in;

import cap.s42academy.user.domain.valueobject.SessionId;

import javax.validation.Valid;

@FunctionalInterface
public interface LoginUserUseCase {

    SessionId handle(@Valid LoginUserCommand command);

}
