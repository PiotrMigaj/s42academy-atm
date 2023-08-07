package cap.s42academy.user.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record LoginUserCommand(
        @NotBlank String userId,
        @NotBlank @Pattern(regexp = "\\d{4}") String pin
) {
}
