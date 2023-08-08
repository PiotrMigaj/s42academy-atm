package cap.s42academy.account.application.port.out;

import cap.s42academy.account.domain.valueobject.AccountId;

@FunctionalInterface
public interface ExistsAccountByIdPort {
    boolean existsBy(AccountId accountId);
}
