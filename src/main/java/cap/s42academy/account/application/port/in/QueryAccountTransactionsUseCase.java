package cap.s42academy.account.application.port.in;

import cap.s42academy.account.domain.valueobject.AccountId;

import java.util.List;

@FunctionalInterface
public interface QueryAccountTransactionsUseCase {
    List<QueryAccountTransactionsProjection> queryBy(AccountId accountId);
}
