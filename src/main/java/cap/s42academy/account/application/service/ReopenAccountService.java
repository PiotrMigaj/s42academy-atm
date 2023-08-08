package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.ReopenAccountUseCase;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ReopenAccountService implements ReopenAccountUseCase {

    static final String ACCOUNT_WITH_ID_DOES_NOT_EXISTS = "Account with ID=%s does not exists!";
    static final String CAN_NOT_REOPEN_ACCOUNT_WHEN_ACCOUNT_STATUS_DIFFERS_FROM_CLOSED = "Can not reopen account when account status differs from CLOSED!";
    private final FindAccountByIdPort findAccountByIdPort;
    private final SaveAccountPort saveAccountPort;
    private final AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;

    @Transactional
    @Override
    public void handle(AccountId accountId) {
        Account account = findAccountByIdPort.findBy(accountId)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue())));
        accountHolderAuthenticationValidator.validate(account);
        if (account.getAccountStatus()!= AccountStatus.CLOSED){
            throw new IllegalStateException(CAN_NOT_REOPEN_ACCOUNT_WHEN_ACCOUNT_STATUS_DIFFERS_FROM_CLOSED);
        }
        account.setAccountStatus(AccountStatus.ACTIVE);
        saveAccountPort.save(account);
    }
}
