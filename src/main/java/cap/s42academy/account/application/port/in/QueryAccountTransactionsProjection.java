package cap.s42academy.account.application.port.in;

import cap.s42academy.account.domain.valueobject.TransactionType;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface QueryAccountTransactionsProjection {

    @Value("#{target.transactionId.getValue().toString()}")
    String getTransactionId();
    BigDecimal getAmount();
    TransactionType getTransactionType();
    LocalDate getDateOfTransaction();
    LocalTime getTimeOfTransaction();
}
