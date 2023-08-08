package cap.s42academy.account.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface OpenAccountUseCase {

    void handle(@Valid OpenAccountCommand command);

}
