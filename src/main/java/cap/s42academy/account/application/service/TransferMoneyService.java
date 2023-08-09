package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.TransferMoneyCommand;
import cap.s42academy.account.application.port.in.TransferMoneyUseCase;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
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
class TransferMoneyService implements TransferMoneyUseCase {

    static final String ACCOUNT_WITH_ID_DOES_NOT_EXISTS = "Account with ID=%s does not exists!";
    public static final String ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS = "Account with ID=%s does not have ACTIVE status!";
    private final FindAccountByIdPort findAccountByIdPort;
    private final AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;
    private final SaveAccountPort saveAccountPort;
    private final TimeProvider timeProvider;
    private final MaxNumberOfTransactionsValidator maxNumberOfTransactionsValidator;

    @Transactional
    @Override
    public void handle(@Valid TransferMoneyCommand command) {
        AccountId sourceAccountId = AccountId.of(UUID.fromString(command.sourceAccountId()));
        Account sourceAccount = findAccountByIdPort.findBy(sourceAccountId)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(sourceAccountId.getValue())));
        accountHolderAuthenticationValidator.validate(sourceAccount);
        if (sourceAccount.getAccountStatus()!= AccountStatus.ACTIVE){
            throw new IllegalStateException(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(sourceAccountId.getValue()));
        }
        maxNumberOfTransactionsValidator.validate(sourceAccountId);
        boolean isWithdrawSuccessful = sourceAccount.withdraw(command.amount(), timeProvider.dateNow(), timeProvider.timeNow(),true);
        if (!isWithdrawSuccessful){
            throw new IllegalArgumentException("Can not withdraw money, invalid amount of money to withdraw!");
        }

        AccountId targetAccountId = AccountId.of(UUID.fromString(command.targetAccountId()));
        Account targetAccount = findAccountByIdPort.findBy(targetAccountId)
                .orElseThrow(() -> new IllegalArgumentException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(targetAccountId.getValue())));
        if (targetAccount.getAccountStatus()!= AccountStatus.ACTIVE){
            throw new IllegalStateException(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(targetAccountId.getValue()));
        }
        targetAccount.deposit(command.amount(),timeProvider.dateNow(),timeProvider.timeNow(),false);

        saveAccountPort.save(sourceAccount);
        saveAccountPort.save(targetAccount);
    }
}
