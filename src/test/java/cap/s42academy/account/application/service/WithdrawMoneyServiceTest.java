package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.WithdrawMoneyCommand;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.PrintReceiptEventPublisherPort;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.common.dto.event.PrinterEvent;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.account;
import static cap.s42academy.account.application.service.WithdrawMoneyService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawMoneyServiceTest {

    @Mock
    private FindAccountByIdPort findAccountByIdPort;
    @Mock
    private AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;
    @Mock
    private SaveAccountPort saveAccountPort;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private MaxNumberOfTransactionsValidator maxNumberOfTransactionsValidator;
    @Mock
    private PrintReceiptEventPublisherPort printReceiptEventPublisherPort;
    @InjectMocks
    private WithdrawMoneyService withdrawMoneyService;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    @Captor
    private ArgumentCaptor<PrinterEvent> printerEventCaptor;

    @Test
    void shouldThrowException_whenAccountDoesNotExist(){
        //given
        AccountId accountId = AccountId.of(UUID.randomUUID());
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->withdrawMoneyService.handle(withdrawMoneyCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenAccountStatusIsNotActive(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->withdrawMoneyService.handle(withdrawMoneyCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(accountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenWithdrawAmountIsLessThanZero(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("-200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->withdrawMoneyService.handle(withdrawMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
    }

    @Test
    void shouldThrowException_whenWithdrawAmountIsEqualZero(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                BigDecimal.ZERO
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->withdrawMoneyService.handle(withdrawMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
    }

    @Test
    void shouldThrowException_whenThereIsNoEnoughMoney(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.01")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->withdrawMoneyService.handle(withdrawMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
    }

    @Test
    void shouldWithdrawMoney(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("250.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        withdrawMoneyService.handle(withdrawMoneyCommand);
        //then
        assertAll(
                ()->verify(saveAccountPort,times(1)).save(accountCaptor.capture()),
                ()->assertThat(accountCaptor.getValue().getBalance()).isEqualTo(new BigDecimal("50.00"))
        );
    }

    @Test
    void shouldWithdrawMoney_whenAfterWithdrawalAccountBalanceIsZero(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        withdrawMoneyService.handle(withdrawMoneyCommand);
        //then
        assertAll(
                ()->verify(saveAccountPort,times(1)).save(accountCaptor.capture()),
                ()->assertThat(accountCaptor.getValue().getBalance()).isEqualTo(new BigDecimal("0.00"))
        );
    }

    @Test
    void shouldPublishPrintReceiptEvent(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("250.00"));
        AccountId accountId = account.getAccountId();
        WithdrawMoneyCommand withdrawMoneyCommand = new WithdrawMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        withdrawMoneyService.handle(withdrawMoneyCommand);
        //then
        assertAll(
                ()->verify(printReceiptEventPublisherPort,times(1)).publish(printerEventCaptor.capture()),
                ()->assertThat(printerEventCaptor.getValue().subject()).isEqualTo(WITHDRAW_MONEY),
                ()->assertThat(printerEventCaptor.getValue().payload()).isEqualTo(SUCCESSFULLY_WITHDRAWN_MONEY_FROM_ACCOUNT_WITH_ID.formatted(accountId.getValue().toString()))
        );
    }

}
