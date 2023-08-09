package cap.s42academy.common.domainevent.api;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}
