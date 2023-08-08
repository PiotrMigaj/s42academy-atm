package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.ExistsOpenSessionForAccountHolderPort;
import cap.s42academy.user.application.port.in.ExistsOpenSessionForUserUseCase;
import cap.s42academy.user.application.port.out.ExistsOpenSessionForUserWithIdPort;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ExistsOpenSessionForAccountHolderAdapter implements ExistsOpenSessionForAccountHolderPort {

    private final ExistsOpenSessionForUserUseCase existsOpenSessionForUserUseCase;
    @Override
    public boolean existsOpenSession(UUID accountHolderId) {
        return existsOpenSessionForUserUseCase.existsOpenSession(accountHolderId);
    }
}
