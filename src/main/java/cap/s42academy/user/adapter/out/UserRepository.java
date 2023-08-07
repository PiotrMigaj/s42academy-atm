package cap.s42academy.user.adapter.out;

import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, UserId> {
    boolean existsByEmail(String email);
}
