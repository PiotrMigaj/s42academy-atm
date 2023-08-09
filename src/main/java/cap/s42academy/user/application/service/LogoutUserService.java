package cap.s42academy.user.application.service;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.user.application.port.in.LogoutUserCommand;
import cap.s42academy.user.application.port.in.LogoutUserUseCase;
import cap.s42academy.user.application.port.out.ExistsUserByIdPort;
import cap.s42academy.user.application.port.out.GetOpenSessionForUserWithIdPort;
import cap.s42academy.user.application.port.out.SaveSessionPort;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

import static cap.s42academy.user.domain.valueobject.SessionStatus.CLOSED;

@Service
@RequiredArgsConstructor
@Validated
class LogoutUserService implements LogoutUserUseCase {

    static final String THERE_IS_NO_USER_WITH_ID = "There is no user with ID=%s";

    private final GetOpenSessionForUserWithIdPort getOpenSessionForUserWithIdPort;
    private final SaveSessionPort saveSessionPort;
    private final ExistsUserByIdPort existsUserByIdPort;
    private final TimeProvider timeProvider;

    @Transactional
    @Override
    public void handle(@Valid LogoutUserCommand command) {
        UserId userId = UserId.of(UUID.fromString(command.userId()));
        if (!existsUserByIdPort.existsBy(userId)){
            throw new EntityNotFoundException(THERE_IS_NO_USER_WITH_ID.formatted(command.userId()));
        }
        Optional<Session> optionalSession =
                getOpenSessionForUserWithIdPort.getOpenSession(userId);
        if (optionalSession.isEmpty()){
            return;
        }
        Session sessionToUpdate = optionalSession.get();
        sessionToUpdate.setClosedAt(timeProvider.dateTimeNow());
        sessionToUpdate.setSessionStatus(CLOSED);
        saveSessionPort.save(sessionToUpdate);
    }
}
