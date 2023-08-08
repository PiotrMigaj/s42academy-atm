package cap.s42academy.account.application.port.out;

import java.util.UUID;

@FunctionalInterface
public interface ExistsOpenSessionForAccountHolderPort {
    boolean existsOpenSession(UUID accountHolderId);
}
