package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.in.ExistsOpenSessionForUserUseCase;
import cap.s42academy.user.application.port.out.ExistsOpenSessionForUserWithIdPort;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ExistsOpenSessionForUserService implements ExistsOpenSessionForUserUseCase {

    private final ExistsOpenSessionForUserWithIdPort existsOpenSessionForUserWithIdPort;
    @Override
    public boolean existsOpenSession(UUID userId) {
        return existsOpenSessionForUserWithIdPort.existsOpenSession(UserId.of(userId));
    }
}
