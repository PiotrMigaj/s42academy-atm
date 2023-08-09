package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.QueryAccountBalanceProjection;
import cap.s42academy.account.application.port.in.QueryAccountBalanceUseCase;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.QueryAccountBalancePort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
class QueryAccountBalanceService implements QueryAccountBalanceUseCase {

    static final String ACCOUNT_WITH_ID_DOES_NOT_EXISTS = "Account with ID=%s does not exists!";
    static final String ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS = "Account with ID=%s does not have ACTIVE status!";
    private final QueryAccountBalancePort queryAccountBalancePort;
    private final FindAccountByIdPort findAccountByIdPort;
    private final AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;

    @Transactional(readOnly = true)
    @Override
    public QueryAccountBalanceProjection queryBy(AccountId accountId) {
        Account account = findAccountByIdPort.findBy(accountId)
                .orElseThrow(() -> new EntityNotFoundException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue())));
        accountHolderAuthenticationValidator.validate(account);
        if (account.getAccountStatus()!= AccountStatus.ACTIVE){
            throw new IllegalStateException(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(accountId.getValue()));
        }
        return queryAccountBalancePort.queryBy(accountId);
    }

}
