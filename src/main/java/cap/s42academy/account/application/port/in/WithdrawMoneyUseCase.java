package cap.s42academy.account.application.port.in;

import javax.validation.Valid;

@FunctionalInterface
public interface WithdrawMoneyUseCase {
    void handle(@Valid WithdrawMoneyCommand command);
}
