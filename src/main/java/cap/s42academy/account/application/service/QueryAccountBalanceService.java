package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.QueryAccountBalanceProjection;
import cap.s42academy.account.application.port.in.QueryAccountBalanceUseCase;
import cap.s42academy.account.application.port.out.ExistsOpenSessionForAccountHolderPort;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.QueryAccountBalancePort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class QueryAccountBalanceService implements QueryAccountBalanceUseCase {

    static final String ACCOUNT_WITH_ID_DOES_NOT_EXISTS = "Account with ID=%s does not exists!";
    static final String USER_WITH_ID_IS_UNAUTHORIZED = "User with ID=%s is unauthorized!";
    private final QueryAccountBalancePort queryAccountBalancePort;
    private final ExistsOpenSessionForAccountHolderPort existsOpenSessionForAccountHolderPort;
    private final FindAccountByIdPort findAccountByIdPort;
    private final OpenSessionForAccountHolderValidator openSessionForAccountHolderValidator;
    @Transactional(readOnly = true)
    @Override
    public QueryAccountBalanceProjection queryBy(AccountId accountId) {
        Account account = findAccountByIdPort.findBy(accountId)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue())));
        openSessionForAccountHolderValidator.validate(account);
        return queryAccountBalancePort.queryBy(accountId);
    }

}
