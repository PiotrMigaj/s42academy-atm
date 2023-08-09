package cap.s42academy.common.dto.event;

import cap.s42academy.common.domainevent.api.DomainEvent;

import java.time.Instant;

public record PrinterEvent(
        Instant occurredOn,
        String subject,
        String payload

) implements DomainEvent {
}
