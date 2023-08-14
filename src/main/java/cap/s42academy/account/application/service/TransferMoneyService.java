package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.TransferMoneyCommand;
import cap.s42academy.account.application.port.in.TransferMoneyUseCase;
import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.application.port.out.PrintReceiptEventPublisherPort;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.common.dto.event.PrinterEvent;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class TransferMoneyService implements TransferMoneyUseCase {

    static final String ACCOUNT_WITH_ID_DOES_NOT_EXISTS = "Account with ID=%s does not exists!";
    static final String ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS = "Account with ID=%s does not have ACTIVE status!";
    static final String TRANSFER_MONEY = "TRANSFER MONEY";
    static final String SUCCESSFULLY_TRANSFERRED_MONEY_FROM_ACCOUNT_WITH_ID_TO_ACCOUNT_WITH_ID = "Successfully transferred money from account with ID=%s to account with ID=%s.";
    static final String CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW = "Can not withdraw money, invalid amount of money to withdraw!";

    private final FindAccountByIdPort findAccountByIdPort;
    private final AccountHolderAuthenticationValidator accountHolderAuthenticationValidator;
    private final SaveAccountPort saveAccountPort;
    private final TimeProvider timeProvider;
    private final MaxNumberOfTransactionsValidator maxNumberOfTransactionsValidator;
    private final PrintReceiptEventPublisherPort printReceiptEventPublisherPort;

    @Transactional
    @Override
    public void handle(@Valid TransferMoneyCommand command) {
        AccountId sourceAccountId = AccountId.of(UUID.fromString(command.sourceAccountId()));
        Account sourceAccount = findAccountByIdPort.findBy(sourceAccountId)
                .orElseThrow(() -> new EntityNotFoundException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(sourceAccountId.getValue())));
        accountHolderAuthenticationValidator.validate(sourceAccount);
        if (sourceAccount.getAccountStatus()!= AccountStatus.ACTIVE){
            throw new IllegalStateException(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(sourceAccountId.getValue()));
        }
        maxNumberOfTransactionsValidator.validate(sourceAccountId);
        boolean isWithdrawSuccessful = sourceAccount.withdraw(command.amount(), timeProvider.dateNow(), timeProvider.timeNow(),true);
        if (!isWithdrawSuccessful){
            throw new IllegalArgumentException(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW);
        }

        AccountId targetAccountId = AccountId.of(UUID.fromString(command.targetAccountId()));
        Account targetAccount = findAccountByIdPort.findBy(targetAccountId)
                .orElseThrow(() -> new EntityNotFoundException(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(targetAccountId.getValue())));
        if (targetAccount.getAccountStatus()!= AccountStatus.ACTIVE){
            throw new IllegalStateException(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(targetAccountId.getValue()));
        }
        targetAccount.deposit(command.amount(),timeProvider.dateNow(),timeProvider.timeNow(),false);

        saveAccountPort.save(sourceAccount);
        saveAccountPort.save(targetAccount);
        publishPrintReceiptEvent(sourceAccountId,targetAccountId);
    }
    private void publishPrintReceiptEvent(AccountId sourceAccountId,AccountId targetAccountId) {
        printReceiptEventPublisherPort.publish(
                new PrinterEvent(
                        Instant.now(),
                        TRANSFER_MONEY,
                        SUCCESSFULLY_TRANSFERRED_MONEY_FROM_ACCOUNT_WITH_ID_TO_ACCOUNT_WITH_ID.formatted(sourceAccountId.getValue(),targetAccountId.getValue())
                )
        );
    }
}
