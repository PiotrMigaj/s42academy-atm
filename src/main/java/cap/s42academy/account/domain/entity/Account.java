package cap.s42academy.account.domain.entity;

import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

import static cap.s42academy.account.domain.valueobject.AccountStatus.ACTIVE;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Account {

    @EmbeddedId
    @AttributeOverride(name = "value", column = @Column(name = "id",columnDefinition = "BINARY(16)"))
    private AccountId accountId;
    @Column(nullable = false,columnDefinition = "BINARY(16)")
    private UUID accountHolderId;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    @Column(precision = 19,scale = 2)
    private BigDecimal balance;

    public static Account createNew(UUID accountHolderId,BigDecimal openingBalance){
        return Account.builder()
                .accountId(AccountId.of(UUID.randomUUID()))
                .accountHolderId(accountHolderId)
                .accountStatus(ACTIVE)
                .balance(openingBalance)
                .build();
    }

}
