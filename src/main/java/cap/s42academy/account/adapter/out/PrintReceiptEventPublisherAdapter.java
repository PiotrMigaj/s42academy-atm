package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.PrintReceiptEventPublisherPort;
import cap.s42academy.common.domainevent.api.DomainEventPublisher;
import cap.s42academy.common.dto.event.PrinterEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class PrintReceiptEventPublisherAdapter implements PrintReceiptEventPublisherPort {

    private final DomainEventPublisher<PrinterEvent> domainEventPublisher;

    @Override
    public void publish(PrinterEvent printerEvent) {
        domainEventPublisher.publish(printerEvent);
    }
}
