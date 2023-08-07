package cap.s42academy.user.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record RegisterUserCommand(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank @Pattern(regexp = "\\d{4}") String pin
) {
}
