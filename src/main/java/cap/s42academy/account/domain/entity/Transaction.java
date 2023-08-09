package cap.s42academy.account.domain.entity;

import cap.s42academy.account.domain.valueobject.TransactionId;
import cap.s42academy.account.domain.valueobject.TransactionType;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@EqualsAndHashCode(of = {"transactionId"})
public class Transaction {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id",columnDefinition = "BINARY(16)"))
    private TransactionId transactionId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private LocalDate dateOfTransaction;
    @Column(nullable = false)
    private LocalTime timeOfTransaction;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
    @Column(columnDefinition = "TINYINT",nullable = false)
    private Boolean isSourceAccountTheSame;

    public static Transaction createNew(
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


}
