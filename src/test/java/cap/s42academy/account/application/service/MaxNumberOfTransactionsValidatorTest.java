package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.out.CountNumberOfTransactionsPerDayPort;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static cap.s42academy.account.application.service.MaxNumberOfTransactionsValidator.MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaxNumberOfTransactionsValidatorTest {

    @Mock
    private AccountProperties accountProperties;
    @Mock
    private CountNumberOfTransactionsPerDayPort countNumberOfTransactionsPerDayPort;
    @Mock
    private TimeProvider timeProvider;
    @InjectMocks
    private MaxNumberOfTransactionsValidator maxNumberOfTransactionsValidator;

    @Test
    void shouldThrowException_whenValidateNumberOfTransactionsPerDay_andNumberOfTransactionsIsEqualToAllowedValue(){
        //given
        AccountId accountId = AccountId.of(UUID.randomUUID());
        LocalDate date = LocalDate.now();
        when(accountProperties.getMaxNumberOfTransactions()).thenReturn(2);
        when(timeProvider.dateNow()).thenReturn(date);
        when(countNumberOfTransactionsPerDayPort.countNumber(accountId, date)).thenReturn(2);
        //when
        //then
        assertThatThrownBy(()->maxNumberOfTransactionsValidator.validate(accountId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED);

    }

}
