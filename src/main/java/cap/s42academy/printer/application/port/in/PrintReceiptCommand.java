package cap.s42academy.printer.application.port.in;

public record PrintReceiptCommand(
        String subject,
        String payload

) {
}
