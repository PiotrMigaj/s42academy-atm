package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.in.QueryAccountBalanceProjection;
import cap.s42academy.account.application.port.out.QueryAccountBalancePort;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class QueryAccountBalanceAdapter implements QueryAccountBalancePort {

    private final AccountRepository accountRepository;
    @Override
    public QueryAccountBalanceProjection queryBy(AccountId accountId) {
        return accountRepository.findByAccountId(accountId);
    }
}
