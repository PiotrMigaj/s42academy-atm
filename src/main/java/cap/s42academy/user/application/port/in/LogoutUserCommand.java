package cap.s42academy.user.application.port.in;

import javax.validation.constraints.NotBlank;

public record LogoutUserCommand(
        @NotBlank(message = "userId can not be blank!") String userId
) {
}
