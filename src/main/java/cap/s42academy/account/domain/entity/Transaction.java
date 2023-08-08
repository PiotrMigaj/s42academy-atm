package cap.s42academy.account.domain.entity;

import cap.s42academy.account.domain.valueobject.TransactionType;
import cap.s42academy.account.domain.valueobject.TransactionId;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Transaction {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id",columnDefinition = "BINARY(16)"))
    private TransactionId transactionId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    @Column(nullable = false)
    private BigDecimal amount;
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;
}
