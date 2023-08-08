package cap.s42academy.user.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface LogoutUserUseCase {
    void handle(@Valid LogoutUserCommand command);
}
