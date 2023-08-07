package cap.s42academy.user.application.port.in;

import cap.s42academy.user.domain.valueobject.UserId;

import javax.validation.Valid;

@FunctionalInterface
public interface RegisterUserUseCase {
    UserId handle(@Valid RegisterUserCommand command);
}
