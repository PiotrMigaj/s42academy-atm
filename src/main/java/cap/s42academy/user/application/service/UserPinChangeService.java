package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.in.UserPinChangeCommand;
import cap.s42academy.user.application.port.in.UserPinChangeUseCase;
import cap.s42academy.user.application.port.out.ExistsOpenSessionForUserWithIdPort;
import cap.s42academy.user.application.port.out.FindUserByIdPort;
import cap.s42academy.user.application.port.out.SaveUserPort;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class UserPinChangeService implements UserPinChangeUseCase {

    static final String THERE_IS_NO_USER_WITH_ID = "There is no user with ID=%s";
    static final String NEW_PIN_VALUE_MUST_DIFFER_FROM_THE_CURRENT_ONE = "New pin value must differ from the current one!";
    static final String USER_WITH_ID_IS_UNAUTHORIZED = "User with ID=%s is unauthorized!";

    private final SaveUserPort saveUserPort;
    private final FindUserByIdPort findUserByIdPort;
    private final PasswordEncoder passwordEncoder;
    private final ExistsOpenSessionForUserWithIdPort existsOpenSessionForUserWithIdPort;

    @Transactional
    @Override
    public void handle(@Valid UserPinChangeCommand command) {
        UserId userId = UserId.of(UUID.fromString(command.userId()));
        validateIfUserIsAuthorized(command, userId);
        User userToUpdate = findUserByIdPort.findBy(userId)
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_USER_WITH_ID.formatted(command.userId())));
        checkIfNewPinValueDifferFromTheCurrentOne(command, userToUpdate);
        userToUpdate.setPin(passwordEncoder.encode(command.pin()));
        saveUserPort.save(userToUpdate);
    }

    private void validateIfUserIsAuthorized(UserPinChangeCommand command, UserId userId) {
        if (!existsOpenSessionForUserWithIdPort.existsOpenSession(userId)){
            throw new IllegalStateException(USER_WITH_ID_IS_UNAUTHORIZED.formatted(command.userId()));
        }
    }

    private void checkIfNewPinValueDifferFromTheCurrentOne(UserPinChangeCommand command, User user) {
        boolean isPinMatchingStoredValue = passwordEncoder.matches(command.pin(), user.getPin());
        if (isPinMatchingStoredValue){
            throw new IllegalArgumentException(NEW_PIN_VALUE_MUST_DIFFER_FROM_THE_CURRENT_ONE);
        }
    }


}
