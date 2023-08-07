package cap.s42academy.user.adapter.out;

import cap.s42academy.user.application.port.out.ExistsUserByEmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExistsUserByEmailAdapter implements ExistsUserByEmailPort {

    private final UserRepository userRepository;
    @Override
    public boolean existsBy(String email) {
        return userRepository.existsByEmail(email);
    }
}
