package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.in.QueryAccountTransactionsProjection;
import cap.s42academy.account.application.port.out.QueryAccountTransactionsPort;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
class QueryAccountTransactionsAdapter implements QueryAccountTransactionsPort {

    private final TransactionRepository transactionRepository;
    @Override
    public List<QueryAccountTransactionsProjection> queryBy(AccountId accountId) {
        return transactionRepository.findTop5AllByAccount_AccountIdOrderByDateOfTransactionDescTimeOfTransactionDesc(accountId);
    }
}
