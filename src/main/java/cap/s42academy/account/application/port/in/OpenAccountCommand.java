package cap.s42academy.account.application.port.in;

import javax.validation.constraints.NotBlank;

public record OpenAccountCommand(
        @NotBlank String accountHolderId
) {
}
