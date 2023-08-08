package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.out.CountNumberOfTransactionsPerDayPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class MaxNumberOfTransactionsValidator {

    static final String MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED = "Maximum number of transactions per day is exceeded!";
    private final AccountProperties accountProperties;
    private final CountNumberOfTransactionsPerDayPort countNumberOfTransactionsPerDayPort;
    private final TimeProvider timeProvider;

    @Transactional
    public void validate(AccountId accountId){
        int numOfTransactionsPerDay = countNumberOfTransactionsPerDayPort.countNumber(accountId, timeProvider.dateNow());
        if (numOfTransactionsPerDay>=accountProperties.getMaxNumberOfTransactions()){
            throw new IllegalArgumentException(MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED);
        }
    }

}
