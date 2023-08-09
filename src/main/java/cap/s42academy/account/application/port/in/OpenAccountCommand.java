package cap.s42academy.account.application.port.in;

import javax.validation.constraints.NotBlank;

public record OpenAccountCommand(
        @NotBlank(message = "accountHolderId can not be blank!") String accountHolderId
) {
}
