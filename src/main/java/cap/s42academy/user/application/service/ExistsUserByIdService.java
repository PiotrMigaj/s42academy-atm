package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.in.ExistsUserByIdUseCase;
import cap.s42academy.user.application.port.out.ExistsUserByIdPort;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ExistsUserByIdService implements ExistsUserByIdUseCase {

    private final ExistsUserByIdPort existsUserByIdPort;
    @Override
    public boolean existsBy(UserId userId) {
        return existsUserByIdPort.existsBy(userId);
    }
}
