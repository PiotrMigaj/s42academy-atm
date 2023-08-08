package cap.s42academy.user.application.port.out;

import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.valueobject.SessionId;

@FunctionalInterface
public interface SaveSessionPort {

    SessionId save(Session session);

}
