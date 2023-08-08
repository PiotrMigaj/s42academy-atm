package cap.s42academy.account.application.port.out;

import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;

@FunctionalInterface
public interface SaveAccountPort {
    AccountId save(Account account);
}
