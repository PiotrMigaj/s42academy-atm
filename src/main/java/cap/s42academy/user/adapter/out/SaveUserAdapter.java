package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.SaveUserPort;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
class SaveUserAdapter implements SaveUserPort {

    private final UserRepository userRepository;
    @Override
    public UserId saveUser(User user) {
        return userRepository.save(user).getUserId();
    }
}
