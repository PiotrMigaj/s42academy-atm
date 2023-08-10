package cap.s42academy.account.domain.entity;

import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.account.domain.valueobject.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.account;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class TransactionTest {

    @Test
    void shouldCreateTransaction(){
        //given
        TransactionType transactionType = TransactionType.DEPOSIT;
        BigDecimal amount = new BigDecimal("200.00");
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        boolean isSourceAccountTheSame = true;
        Account account = account(
                UUID.randomUUID(),
                AccountStatus.ACTIVE,
                new BigDecimal("200.00")
        );
        //when
        Transaction result = Transaction.createNew(
                transactionType,
                amount,
                date,
                time,
                account,
                isSourceAccountTheSame
        );
        //then
        assertAll(
                ()->assertThat(result.getTransactionId()).isNotNull(),
                ()->assertThat(result.getTransactionType()).isEqualTo(transactionType),
                ()->assertThat(result.getAmount()).isEqualTo(amount),
                ()->assertThat(result.getDateOfTransaction()).isEqualTo(date),
                ()->assertThat(result.getTimeOfTransaction()).isEqualTo(time),
                ()->assertThat(result.getAccount().getAccountId()).isEqualTo(account.getAccountId()),
                ()->assertThat(result.getIsSourceAccountTheSame()).isEqualTo(isSourceAccountTheSame)
        );
    }

}
