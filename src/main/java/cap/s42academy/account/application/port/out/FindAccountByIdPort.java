package cap.s42academy.account.application.port.out;

import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;

import java.util.Optional;

@FunctionalInterface
public interface FindAccountByIdPort {
    Optional<Account> findBy(AccountId accountId);
}
