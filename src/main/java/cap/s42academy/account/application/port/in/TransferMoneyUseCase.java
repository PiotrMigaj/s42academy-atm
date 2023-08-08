package cap.s42academy.account.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface TransferMoneyUseCase {
    void handle(@Valid TransferMoneyCommand command);
}
