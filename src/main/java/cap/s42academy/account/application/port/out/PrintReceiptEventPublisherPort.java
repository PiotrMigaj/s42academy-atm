package cap.s42academy.account.application.port.out;

import cap.s42academy.common.dto.event.PrinterEvent;

@FunctionalInterface
public interface PrintReceiptEventPublisherPort {
    void publish(PrinterEvent printerEvent);
}
