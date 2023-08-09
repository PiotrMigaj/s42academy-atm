package cap.s42academy.printer.adapter.in;

import cap.s42academy.common.dto.event.PrinterEvent;
import cap.s42academy.printer.application.port.in.PrintReceiptCommand;
import cap.s42academy.printer.application.port.in.PrintReceiptUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
class PrintReceiptEventListenerAdapter {

    private final PrintReceiptUseCase printReceiptUseCase;

    @TransactionalEventListener
    void handlePrinterEvent(PrinterEvent printerEvent){
        PrintReceiptCommand printReceiptCommand = new PrintReceiptCommand(printerEvent.subject(), printerEvent.payload());
        printReceiptUseCase.handle(printReceiptCommand);
    }

}
