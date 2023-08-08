package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.ExistsUserByEmailPort;
import cap.s42academy.user.application.port.out.ExistsUserByIdPort;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExistsUserByIdAdapter implements ExistsUserByIdPort {

    private final UserRepository userRepository;

    @Override
    public boolean existsBy(UserId userId) {
        return userRepository.existsById(userId);
    }
}
