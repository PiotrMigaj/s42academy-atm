package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.in.RegisterUserCommand;
import cap.s42academy.user.application.port.in.RegisterUserUseCase;
import cap.s42academy.user.application.port.out.ExistsUserByEmailPort;
import cap.s42academy.user.application.port.out.SaveUserPort;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class RegisterUserService implements RegisterUserUseCase {

    private static final String USER_WITH_EMAIL_ALREADY_EXISTS = "User with email: %s already exists!";
    private final SaveUserPort saveUserPort;
    private final ExistsUserByEmailPort existsUserByEmailPort;

    @Transactional
    @Override
    public String registerUser(@Valid RegisterUserCommand command) {
        String email = command.email();
        if (existsUserByEmailPort.existsUserByEmail(email)){
            throw new IllegalArgumentException(USER_WITH_EMAIL_ALREADY_EXISTS.formatted(email));
        }
        User userToPersist = User.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(email)
                .pin(command.pin())
                .build();
        return saveUserPort.saveUser(userToPersist).toString();
    }
}
