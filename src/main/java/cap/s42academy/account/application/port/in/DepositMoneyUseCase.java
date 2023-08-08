package cap.s42academy.account.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface DepositMoneyUseCase {
    void handle(@Valid DepositMoneyCommand command);
}
