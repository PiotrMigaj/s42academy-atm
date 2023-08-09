package cap.s42academy.printer.application.service;

import cap.s42academy.printer.application.port.in.PrintReceiptCommand;
import cap.s42academy.printer.application.port.in.PrintReceiptUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class PrintReceiptService implements PrintReceiptUseCase {

    @Override
    public void handle(PrintReceiptCommand command) {
        log.info("Printing receipt with body: {}",command);
    }
}
