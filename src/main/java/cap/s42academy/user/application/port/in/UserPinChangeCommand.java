package cap.s42academy.user.application.port.in;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record UserPinChangeCommand(
        @NotBlank(message = "userId can not be blank!") String userId,
        @NotBlank(message = "PIN can not be blank!") @Pattern(regexp = "\\d{4}",message = "PIN must contain 4 digits!") String pin
) {
}
