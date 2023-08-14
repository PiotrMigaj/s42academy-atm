package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.QueryAccountTransactionsPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.account;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryAccountTransactionsTest {

    @Mock
    private QueryAccountTransactionsPort queryAccountTransactionsPort;
    @Mock
    private FindAccountByIdPort findAccountByIdPort;
    @Mock
    private AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;
    @InjectMocks
    private QueryAccountTransactionsService queryAccountTransactionsService;

    @Test
    void shouldThrowException_whenAccountDoesNotExist(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        when(findAccountByIdPort.findBy(account.getAccountId())).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->queryAccountTransactionsService.queryBy(account.getAccountId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldThrowException_whenAccountStatusIsNotActive(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("120.00"));
        when(findAccountByIdPort.findBy(account.getAccountId())).thenReturn(Optional.of(account));
        //when
        //then
        assertThatThrownBy(()->queryAccountTransactionsService.queryBy(account.getAccountId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldReturnAccountTransactions(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00"));
        when(findAccountByIdPort.findBy(account.getAccountId())).thenReturn(Optional.of(account));
        //when
        queryAccountTransactionsService.queryBy(account.getAccountId());
        //then
        verify(queryAccountTransactionsPort,times(1)).queryBy(account.getAccountId());
    }
}
