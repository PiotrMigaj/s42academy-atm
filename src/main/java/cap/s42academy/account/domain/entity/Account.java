package cap.s42academy.account.domain.entity;

import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.account.domain.valueobject.TransactionType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static cap.s42academy.account.domain.valueobject.AccountStatus.ACTIVE;
import static cap.s42academy.account.domain.valueobject.TransactionType.DEPOSIT;
import static cap.s42academy.account.domain.valueobject.TransactionType.WITHDRAWAL;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Account {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id",columnDefinition = "BINARY(16)"))
    private AccountId accountId;
    @Version
    private Long version;
    @Column(nullable = false,columnDefinition = "BINARY(16)")
    private UUID accountHolderId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Column(precision = 19,scale = 2)
    private BigDecimal balance;
    @OneToMany(mappedBy = "account",cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    public static Account createNew(UUID accountHolderId,BigDecimal openingBalance){
        return Account.builder()
                .accountId(AccountId.of(UUID.randomUUID()))
                .accountHolderId(accountHolderId)
                .accountStatus(ACTIVE)
                .balance(openingBalance)
                .build();
    }

    public boolean deposit(BigDecimal amount, LocalDate dateOfTransaction, LocalTime timeOfTransaction){
        if (isNegativeOrZero(amount)){
            return false;
        }
        this.balance = this.balance.add(amount);
        Transaction transaction = Transaction.createNew(DEPOSIT, amount, dateOfTransaction,timeOfTransaction,this);
        this.transactions.add(transaction);
        return true;
    }

    public boolean withdraw(BigDecimal amount, LocalDate dateOfTransaction, LocalTime timeOfTransaction){
        if (isNegativeOrZero(amount)){
            return false;
        }
        if (!mayWithdraw(amount)){
            return false;
        }
        this.balance = this.balance.subtract(amount);
        Transaction transaction = Transaction.createNew(WITHDRAWAL, amount, dateOfTransaction,timeOfTransaction,this);
        this.transactions.add(transaction);
        return true;
    }

    private boolean isNegativeOrZero(BigDecimal amount){
        return amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isNegative(BigDecimal amount){
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private boolean mayWithdraw(BigDecimal amount) {
        BigDecimal subtracted = this.balance.subtract(amount);
        return !isNegative(subtracted);
    }



}
