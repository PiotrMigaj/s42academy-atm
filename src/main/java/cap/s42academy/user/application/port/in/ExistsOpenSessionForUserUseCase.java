package cap.s42academy.user.application.port.in;

import java.util.UUID;

@FunctionalInterface
public interface ExistsOpenSessionForUserUseCase {
    boolean existsOpenSession(UUID userId);
}
