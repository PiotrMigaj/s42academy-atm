package cap.s42academy.account.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record WithdrawMoneyCommand(
        @NotBlank String accountId,
        @NotNull BigDecimal amount
        ) {
}