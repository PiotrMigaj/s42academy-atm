package cap.s42academy.user.adapter.out;

import cap.s42academy.account.application.port.in.OpenAccountCommand;
import cap.s42academy.account.application.port.in.OpenAccountUseCase;
import cap.s42academy.user.application.port.out.OpenAccountPort;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class OpenAccountAdapter implements OpenAccountPort {

    private final OpenAccountUseCase openAccountUseCase;
    @Override
    public void openAccountForUser(UserId userId) {
        openAccountUseCase.handle(new OpenAccountCommand(userId.getValue().toString()));
    }
}
