package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.CountNumberOfTransactionsPerDayPort;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
class CountNumberOfTransactionsPerDayAdapter implements CountNumberOfTransactionsPerDayPort {

    private final TransactionRepository transactionRepository;
    @Override
    public int countNumber(AccountId accountId, LocalDate date) {
        return transactionRepository.countTransactionsByDateOfTransactionAndAccount_AccountIdAndIsSourceAccountTheSame(date,accountId,true);
    }
}
