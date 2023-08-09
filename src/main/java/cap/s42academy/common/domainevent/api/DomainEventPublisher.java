package cap.s42academy.common.domainevent.api;

public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T event);
}
