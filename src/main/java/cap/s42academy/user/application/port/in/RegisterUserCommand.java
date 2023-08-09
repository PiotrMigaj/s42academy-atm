package cap.s42academy.user.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record RegisterUserCommand(
        @NotBlank(message = "firstName can not be blank!") String firstName,
        @NotBlank(message = "lastName can not be blank!") String lastName,
        @NotBlank(message = "email can not be blank!") String email,
        @NotBlank(message = "PIN can not be blank!") @Pattern(regexp = "\\d{4}",message = "PIN must contain 4 digits!") String pin
) {
}
