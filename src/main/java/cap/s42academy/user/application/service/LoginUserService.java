package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.in.LoginUserCommand;
import cap.s42academy.user.application.port.in.LoginUserUseCase;
import cap.s42academy.user.application.port.out.ExistsUserByIdPort;
import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class LoginUserService implements LoginUserUseCase {

    private final ExistsUserByIdPort existsUserByIdPort;

    @Transactional
    @Override
    public SessionId handle(LoginUserCommand command) {
        UserId userId = UserId.of(UUID.fromString(command.userId()));
        if (!existsUserByIdPort.existsBy(userId)){
            throw new IllegalArgumentException("There is no user with ID=%s".formatted(command.userId()));
        }
        return null;
    }
}
