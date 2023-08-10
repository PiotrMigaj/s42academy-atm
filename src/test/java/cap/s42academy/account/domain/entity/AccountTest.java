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

class AccountTest {

    @Test
    void shouldCreateAccount(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        //when
        Account result = Account.createNew(accountHolderId, openingBalance);
        //then
        assertAll(
                ()->assertThat(result.getAccountId()).isNotNull(),
                ()->assertThat(result.getAccountHolderId()).isEqualTo(accountHolderId),
                ()->assertThat(result.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE),
                ()->assertThat(result.getBalance()).isEqualTo(openingBalance)
        );
    }

    @Test
    void shouldReturnFalse_whenDepositMoney_andAmountIsZero(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = BigDecimal.ZERO;
        //when
        boolean result = account.deposit(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalse_whenDepositMoney_andAmountIsLessThanZero(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("-100.00");
        //when
        boolean result = account.deposit(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldDepositMoney(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("100.00");
        //when
        boolean result = account.deposit(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertAll(
                ()->assertThat(result).isTrue(),
                ()->assertThat(account.getBalance()).isEqualTo(new BigDecimal("200.00"))
        );
    }

    @Test
    void shouldCreateNewTransaction_whenDepositMoney(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("100.00");
        //when
        account.deposit(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertAll(
                ()->assertThat(account.getTransactions()).hasSize(1),
                ()->assertThat(account.getTransactions().iterator().next().getTransactionId()).isNotNull(),
                ()->assertThat(account.getTransactions().iterator().next().getAmount()).isEqualTo(money),
                ()->assertThat(account.getTransactions().iterator().next().getTransactionType()).isEqualTo(TransactionType.DEPOSIT),
                ()->assertThat(account.getTransactions().iterator().next().getAccount().getAccountId()).isEqualTo(account.getAccountId())
        );
    }

    @Test
    void shouldReturnFalse_whenWithdrawMoney_andAmountIsZero(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = BigDecimal.ZERO;
        //when
        boolean result = account.withdraw(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalse_whenWithdrawMoney_andAmountIsLessThanZero(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("-100.00");
        //when
        boolean result = account.withdraw(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalse_whenWithdrawMoney_andBalanceAfterWithdrawalIsLessThanZero(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("100.01");
        //when
        boolean result = account.withdraw(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnTrue_whenWithdrawMoney_andBalanceAfterWithdrawalIsZero(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("100.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("100.00");
        //when
        boolean result = account.withdraw(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldWithdrawMoney(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("1000.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("100.00");
        //when
        boolean result = account.withdraw(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertAll(
                ()->assertThat(result).isTrue(),
                ()->assertThat(account.getBalance()).isEqualTo(new BigDecimal("900.00"))
        );
    }

    @Test
    void shouldCreateNewTransaction_whenWithdrawMoney(){
        //given
        UUID accountHolderId = UUID.randomUUID();
        BigDecimal openingBalance = new BigDecimal("1000.00");
        Account account = account(accountHolderId, AccountStatus.ACTIVE, openingBalance);
        BigDecimal money = new BigDecimal("100.00");
        //when
        account.withdraw(money, LocalDate.now(), LocalTime.now(), true);
        //then
        assertAll(
                ()->assertThat(account.getTransactions()).hasSize(1),
                ()->assertThat(account.getTransactions().iterator().next().getTransactionId()).isNotNull(),
                ()->assertThat(account.getTransactions().iterator().next().getAmount()).isEqualTo(money),
                ()->assertThat(account.getTransactions().iterator().next().getTransactionType()).isEqualTo(TransactionType.WITHDRAWAL),
                ()->assertThat(account.getTransactions().iterator().next().getAccount().getAccountId()).isEqualTo(account.getAccountId())
        );
    }

}
