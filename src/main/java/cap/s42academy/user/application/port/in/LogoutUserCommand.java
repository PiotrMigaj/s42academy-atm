package cap.s42academy.user.application.port.in;

import javax.validation.constraints.NotBlank;

public record LogoutUserCommand(
        @NotBlank String userId
) {
}
