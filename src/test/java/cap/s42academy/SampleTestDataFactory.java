package cap.s42academy;

import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.entity.Transaction;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.account.domain.valueobject.TransactionId;
import cap.s42academy.account.domain.valueobject.TransactionType;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.SessionStatus;
import cap.s42academy.user.domain.valueobject.UserId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SampleTestDataFactory {


    public static final String PIN_VALUE = "0000";

    public static User user() {
        return User.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString())
                .pin(PIN_VALUE)
                .build();
    }

    public static User userWithoutPin(String pin) {
        return User.builder()
                .userId(UserId.of(UUID.randomUUID()))
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString())
                .pin(pin)
                .build();
    }

    public static User userWithoutIdAndWithoutPin(UserId userId,String pin) {
        return User.builder()
                .userId(userId)
                .firstName(UUID.randomUUID().toString())
                .lastName(UUID.randomUUID().toString())
                .email(UUID.randomUUID().toString())
                .pin(pin)
                .build();
    }

    public static Session session(
            SessionStatus sessionStatus,
            LocalDateTime createdAt,
            LocalDateTime closedAt,
            User user
    ){
        return Session.builder()
                .sessionId(SessionId.of(UUID.randomUUID()))
                .sessionStatus(sessionStatus)
                .createdAt(createdAt)
                .closedAt(closedAt)
                .user(user)
                .build();
    }

    public static Account account(
            UUID accountHolderId,
            AccountStatus accountStatus,
            BigDecimal balance
    ){
        return Account.builder()
                .accountId(AccountId.of(UUID.randomUUID()))
                .accountHolderId(accountHolderId)
                .accountStatus(accountStatus)
                .balance(balance)
                .build();
    }

    public static Transaction transaction(
            TransactionType transactionType,
            BigDecimal amount,
            LocalDate dateOfTransaction,
            LocalTime timeOfTransaction,
            Account account,
            Boolean isSourceAccountTheSame
    ){
        return Transaction.builder()
                .transactionId(TransactionId.of(UUID.randomUUID()))
                .transactionType(transactionType)
                .amount(amount)
                .dateOfTransaction(dateOfTransaction)
                .timeOfTransaction(timeOfTransaction)
                .account(account)
                .isSourceAccountTheSame(isSourceAccountTheSame)
                .build();
    }

    public static Maintenance maintenance(
            MaintenanceStatus maintenanceStatus,
            LocalDateTime startedAt,
            LocalDateTime finishedAt
    ){
        return Maintenance.builder()
                .maintenanceStatus(maintenanceStatus)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .build();
    }



}
