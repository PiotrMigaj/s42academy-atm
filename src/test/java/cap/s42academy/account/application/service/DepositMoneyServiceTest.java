package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.DepositMoneyCommand;
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
import static cap.s42academy.account.application.service.DepositMoneyService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositMoneyServiceTest {

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
    private DepositMoneyService depositMoneyService;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    @Captor
    private ArgumentCaptor<PrinterEvent> printerEventCaptor;

    @Test
    void shouldThrowException_whenUserDoesNotExist(){
        //given
        AccountId accountId = AccountId.of(UUID.randomUUID());
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->depositMoneyService.handle(depositMoneyCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenAccountStatusIsNotActive(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->depositMoneyService.handle(depositMoneyCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(accountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenDepositAmountIsLessThanZero(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("-200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->depositMoneyService.handle(depositMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_DEPOSIT_MONEY_INVALID_AMOUNT_OF_MONEY_TO_DEPOSIT);
    }

    @Test
    void shouldThrowException_whenDepositAmountIsEqualZero(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(
                accountId.getValue().toString(),
                BigDecimal.ZERO
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->depositMoneyService.handle(depositMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_DEPOSIT_MONEY_INVALID_AMOUNT_OF_MONEY_TO_DEPOSIT);
    }

    @Test
    void shouldDepositMoney(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        depositMoneyService.handle(depositMoneyCommand);
        //then
        assertAll(
                ()->verify(saveAccountPort,times(1)).save(accountCaptor.capture()),
                ()->assertThat(accountCaptor.getValue().getBalance()).isEqualTo(new BigDecimal("400.00"))
        );
    }

    @Test
    void shouldPublishPrintReceiptEvent(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        DepositMoneyCommand depositMoneyCommand = new DepositMoneyCommand(
                accountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.of(account));
        //when
        depositMoneyService.handle(depositMoneyCommand);
        //then
        assertAll(
                ()->verify(printReceiptEventPublisherPort,times(1)).publish(printerEventCaptor.capture()),
                ()->assertThat(printerEventCaptor.getValue().subject()).isEqualTo(DEPOSIT_MONEY),
                ()->assertThat(printerEventCaptor.getValue().payload()).isEqualTo(SUCCESSFULLY_DEPOSITED_MONEY_TO_ACCOUNT_WITH_ID.formatted(accountId.getValue().toString()))
        );
    }




}
