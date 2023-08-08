package cap.s42academy.account.application.port.out;

import cap.s42academy.account.application.port.in.QueryAccountTransactionsProjection;
import cap.s42academy.account.domain.valueobject.AccountId;

import java.util.List;

@FunctionalInterface
public interface QueryAccountTransactionsPort {
    List<QueryAccountTransactionsProjection> queryBy(AccountId accountId);
}
