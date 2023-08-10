package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.out.ExistsOpenSessionForAccountHolderPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.common.exception.api.UserUnauthenticatedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.account;
import static cap.s42academy.account.application.service.AccountHolderAuthenticationValidator.USER_WITH_ID_IS_UNAUTHENTICATED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AccountHolderAuthenticationValidatorTest {

    @Mock
    private ExistsOpenSessionForAccountHolderPort existsOpenSessionForAccountHolderPort;
    @InjectMocks
    private AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;

    @Test
    void shouldThrowException_whenValidateUserSession_andThereIsNoOpenSessionForUser(){
        //given
        Account account = account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("200.00"));
        //when
        //then
        assertThatThrownBy(()->accountHolderAuthenticationValidator.validate(account))
                .isInstanceOf(UserUnauthenticatedException.class)
                .hasMessage(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(account.getAccountHolderId().toString()));
    }
}
