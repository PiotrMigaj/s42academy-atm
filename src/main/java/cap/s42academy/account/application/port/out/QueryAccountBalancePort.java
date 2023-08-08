package cap.s42academy.account.application.port.out;

import cap.s42academy.account.application.port.in.QueryAccountBalanceProjection;
import cap.s42academy.account.domain.valueobject.AccountId;

@FunctionalInterface
public interface QueryAccountBalancePort {

    QueryAccountBalanceProjection queryBy(AccountId accountId);

}
