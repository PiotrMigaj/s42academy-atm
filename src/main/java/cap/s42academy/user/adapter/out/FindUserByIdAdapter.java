package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.FindUserByIdPort;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class FindUserByIdAdapter implements FindUserByIdPort {

    private final UserRepository userRepository;
    @Override
    public Optional<User> findBy(UserId userId) {
        return userRepository.findById(userId);
    }
}
