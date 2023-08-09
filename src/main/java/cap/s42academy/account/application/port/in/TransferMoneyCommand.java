package cap.s42academy.account.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public record TransferMoneyCommand(
        @NotBlank(message = "sourceAccountId can not be blank!") String sourceAccountId,
        @NotBlank(message = "targetAccountId can not be blank!") String targetAccountId,
        @NotNull(message = "amount can not be null!") BigDecimal amount
        ) {
}
