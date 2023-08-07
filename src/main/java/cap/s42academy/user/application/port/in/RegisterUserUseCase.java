package cap.s42academy.user.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface RegisterUserUseCase {
    String registerUser(@Valid RegisterUserCommand command);
}
