package cap.s42academy.printer.application.port.in;

@FunctionalInterface
public interface PrintReceiptUseCase {
    void handle(PrintReceiptCommand command);
}
