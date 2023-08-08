package cap.s42academy.user.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface UserPinChangeUseCase {
    void handle(@Valid UserPinChangeCommand command);
}
