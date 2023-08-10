package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
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
import static cap.s42academy.account.application.service.CloseAccountService.ACCOUNT_WITH_ID_DOES_NOT_EXISTS;
import static cap.s42academy.account.application.service.ReopenAccountService.CAN_NOT_REOPEN_ACCOUNT_WHEN_ACCOUNT_STATUS_DIFFERS_FROM_CLOSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReopenAccountServiceTest {

    @Mock
    private FindAccountByIdPort findAccountByIdPort;
    @Mock
    private SaveAccountPort saveAccountPort;
    @Mock
    private AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;
    @InjectMocks
    private ReopenAccountService reopenAccountService;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Test
    void shouldThrowException_whenThereIsNoAccountWithGivenId(){
        //given
        AccountId accountId = AccountId.of(UUID.randomUUID());
        when(findAccountByIdPort.findBy(accountId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->reopenAccountService.handle(accountId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue().toString()));
    }

    @Test
    void shouldThrowException_whenAccountStatusIsNotClosed(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        when(findAccountByIdPort.findBy(account.getAccountId())).thenReturn(Optional.ofNullable(account));
        //when
        //then
        assertThatThrownBy(()->reopenAccountService.handle(accountId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(CAN_NOT_REOPEN_ACCOUNT_WHEN_ACCOUNT_STATUS_DIFFERS_FROM_CLOSED);
    }

    @Test
    void shouldReopenAccount(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("200.00"));
        AccountId accountId = account.getAccountId();
        when(findAccountByIdPort.findBy(account.getAccountId())).thenReturn(Optional.ofNullable(account));
        //when
        reopenAccountService.handle(accountId);
        //then
        assertAll(
                ()->verify(accountHolderAuthenticationValidator,times(1)).validate(account),
                ()->verify(saveAccountPort,times(1)).save(accountCaptor.capture()),
                ()->assertThat(accountCaptor.getValue().getAccountStatus()).isEqualTo(AccountStatus.ACTIVE)
        );
    }
}
