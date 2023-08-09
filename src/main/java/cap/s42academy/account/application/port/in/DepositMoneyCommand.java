package cap.s42academy.account.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record DepositMoneyCommand(
        @NotBlank(message = "accountId can not be blank!") String accountId,
        @NotNull(message = "amount can not be null!") BigDecimal amount
        ) {
}
