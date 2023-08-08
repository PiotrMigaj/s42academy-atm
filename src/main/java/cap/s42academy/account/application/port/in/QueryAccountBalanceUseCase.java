package cap.s42academy.account.application.port.in;

import cap.s42academy.account.domain.valueobject.AccountId;

@FunctionalInterface
public interface QueryAccountBalanceUseCase {
    QueryAccountBalanceProjection queryBy(AccountId accountId);
}
