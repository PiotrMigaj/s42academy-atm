package cap.s42academy.user.application.service;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.user.application.port.in.LoginUserCommand;
import cap.s42academy.user.application.port.in.LoginUserUseCase;
import cap.s42academy.user.application.port.out.FindUserByIdPort;
import cap.s42academy.user.application.port.out.GetOpenSessionForUserWithIdPort;
import cap.s42academy.user.application.port.out.SaveSessionPort;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.SessionStatus;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class LoginUserService implements LoginUserUseCase {

    static final String THERE_IS_NO_USER_WITH_ID = "There is no user with ID=%s";
    static final String PIN_VALUE_DOES_NOT_MATCH_THE_STORED_ONE = "PIN value does not match the stored one!";

    private final FindUserByIdPort findUserByIdPort;
    private final SaveSessionPort saveSessionPort;
    private final TimeProvider timeProvider;
    private final PasswordEncoder passwordEncoder;
    private final GetOpenSessionForUserWithIdPort getOpenSessionForUserWithIdPort;

    @Transactional
    @Override
    public SessionId handle(LoginUserCommand command) {
        UserId userId = UserId.of(UUID.fromString(command.userId()));
        User user = findUserByIdPort.findBy(userId)
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_USER_WITH_ID.formatted(command.userId())));
        checkIfPinValueMatchesStoredOne(command, user);
        Optional<Session> optionalSession = getOpenSessionForUserWithIdPort.getOpenSession(userId);
        if (optionalSession.isPresent()){
            return optionalSession.get().getSessionId();
        }
        Session sessionToSave = Session.builder()
                .sessionId(SessionId.of(UUID.randomUUID()))
                .sessionStatus(SessionStatus.OPEN)
                .createdAt(timeProvider.dateTimeNow())
                .user(user)
                .build();
        return saveSessionPort.save(sessionToSave);
    }

    private void checkIfPinValueMatchesStoredOne(LoginUserCommand command, User user) {
        boolean isPinMatchingStoredValue = passwordEncoder.matches(command.pin(), user.getPin());
        if (!isPinMatchingStoredValue){
            throw new IllegalArgumentException(PIN_VALUE_DOES_NOT_MATCH_THE_STORED_ONE);
        }
    }
}
