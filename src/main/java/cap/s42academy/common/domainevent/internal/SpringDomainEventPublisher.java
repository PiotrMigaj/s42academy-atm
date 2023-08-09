package cap.s42academy.common.domainevent.internal;


import cap.s42academy.common.domainevent.api.DomainEvent;
import cap.s42academy.common.domainevent.api.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class SpringDomainEventPublisher<T extends DomainEvent> implements DomainEventPublisher<T> {

    private final ApplicationEventPublisher applicationEventPublisher;
    @Override
    public void publish(T event) {
        applicationEventPublisher.publishEvent(event);
    }
}
