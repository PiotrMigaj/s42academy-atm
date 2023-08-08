package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.DepositMoneyCommand;
import cap.s42academy.account.application.port.in.DepositMoneyUseCase;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class DepositMoneyService implements DepositMoneyUseCase {

    static final String ACCOUNT_WITH_ID_DOES_NOT_EXISTS = "Account with ID=%s does not exists!";
    static final String AMOUNT_OF_DEPOSIT_MUST_BE_GREATER_THAN_0 = "Amount of deposit must be greater than 0!";
    private final FindAccountByIdPort findAccountByIdPort;
    private final OpenSessionForAccountHolderValidator openSessionForAccountHolderValidator;
    private final SaveAccountPort saveAccountPort;
    private final TimeProvider timeProvider;
    private final MaxNumberOfTransactionsValidator maxNumberOfTransactionsValidator;


    @Transactional
    @Override
    public void handle(@Valid DepositMoneyCommand command) {
        AccountId accountId = AccountId.of(UUID.fromString(command.accountId()));
        Account account = findAccountByIdPort.findBy(accountId)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId.getValue())));
        maxNumberOfTransactionsValidator.validate(accountId);
        openSessionForAccountHolderValidator.validate(account);
        if (!account.deposit(command.amount(),timeProvider.dateNow(),timeProvider.timeNow())){
            throw new IllegalArgumentException(AMOUNT_OF_DEPOSIT_MUST_BE_GREATER_THAN_0);
        }
        saveAccountPort.save(account);
    }
}
