package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.TransferMoneyCommand;
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
import static cap.s42academy.account.application.service.TransferMoneyService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferMoneyServiceTest {

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
    private TransferMoneyService transferMoneyService;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;
    @Captor
    private ArgumentCaptor<PrinterEvent> printerEventCaptor;

    @Test
    void shouldThrowException_whenSourceAccountIdDoesNotExist(){
        //given
        AccountId sourceAccountId = AccountId.of(UUID.randomUUID());
        AccountId targerAccountId = AccountId.of(UUID.randomUUID());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(sourceAccountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenSourceAccountStatusIsNotActive(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        AccountId targerAccountId = AccountId.of(UUID.randomUUID());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("200.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(sourceAccountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenTransferMoneyAmountIsLessThanZero(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        AccountId targerAccountId = AccountId.of(UUID.randomUUID());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("-200.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
    }

    @Test
    void shouldThrowException_whenTransferMoneyAmountIsEqualZero(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        AccountId targerAccountId = AccountId.of(UUID.randomUUID());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                BigDecimal.ZERO
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
    }

    @Test
    void shouldThrowException_whenThereIsNoEnoughMoneyInTheSourceAccount(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        AccountId targerAccountId = AccountId.of(UUID.randomUUID());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("120.01")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
    }

    @Test
    void shouldThrowException_whenTargetAccountIdDoesNotExist(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        AccountId targerAccountId = AccountId.of(UUID.randomUUID());
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("100.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(findAccountByIdPort.findBy(targerAccountId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(targerAccountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenTargetAccountStatusIsNotActive(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        Account targetAccount = account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("120.00"));
        AccountId targerAccountId = targetAccount.getAccountId();
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("100.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(findAccountByIdPort.findBy(targerAccountId)).thenReturn(Optional.of(targetAccount));
        //when
        //then
        assertThatThrownBy(()->transferMoneyService.handle(transferMoneyCommand))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(targerAccountId.getValue().toString()));
    }

    @Test
    void shouldTransferMoney(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        Account targetAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId targerAccountId = targetAccount.getAccountId();
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("100.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(findAccountByIdPort.findBy(targerAccountId)).thenReturn(Optional.of(targetAccount));
        //when
        transferMoneyService.handle(transferMoneyCommand);
        //then
        assertAll(
                ()->verify(saveAccountPort,times(2)).save(accountCaptor.capture()),
                ()->assertThat(accountCaptor.getAllValues().size()).isEqualTo(2),
                ()->assertThat(accountCaptor.getAllValues().get(0).getAccountId()).isEqualTo(sourceAccountId),
                ()->assertThat(accountCaptor.getAllValues().get(0).getBalance()).isEqualTo(new BigDecimal("20.00")),
                ()->assertThat(accountCaptor.getAllValues().get(1).getAccountId()).isEqualTo(targerAccountId),
                ()->assertThat(accountCaptor.getAllValues().get(1).getBalance()).isEqualTo(new BigDecimal("220.00"))
        );
    }

    @Test
    void shouldPublishPrintReceiptEvent(){
        //given
        Account sourceAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId sourceAccountId = sourceAccount.getAccountId();
        Account targetAccount = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        AccountId targerAccountId = targetAccount.getAccountId();
        TransferMoneyCommand transferMoneyCommand = new TransferMoneyCommand(
                sourceAccountId.getValue().toString(),
                targerAccountId.getValue().toString(),
                new BigDecimal("100.00")
        );
        when(findAccountByIdPort.findBy(sourceAccountId)).thenReturn(Optional.of(sourceAccount));
        when(findAccountByIdPort.findBy(targerAccountId)).thenReturn(Optional.of(targetAccount));
        //when
        transferMoneyService.handle(transferMoneyCommand);
        //then
        assertAll(
                ()->verify(printReceiptEventPublisherPort,times(1)).publish(printerEventCaptor.capture()),
                ()->assertThat(printerEventCaptor.getValue().subject()).isEqualTo(TRANSFER_MONEY),
                ()->assertThat(printerEventCaptor.getValue().payload()).isEqualTo(SUCCESSFULLY_TRANSFERRED_MONEY_FROM_ACCOUNT_WITH_ID_TO_ACCOUNT_WITH_ID.formatted(sourceAccountId.getValue().toString(),targerAccountId.getValue().toString()))
        );
    }

}
