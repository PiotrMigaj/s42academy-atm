package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.in.QueryAccountBalanceProjection;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import org.springframework.data.jpa.repository.JpaRepository;

interface AccountRepository extends JpaRepository<Account, AccountId> {
    QueryAccountBalanceProjection findByAccountId(AccountId accountId);
}
