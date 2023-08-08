package cap.s42academy.account.application.port.out;

import cap.s42academy.account.domain.valueobject.AccountId;

import java.time.LocalDate;

@FunctionalInterface
public interface CountNumberOfTransactionsPerDayPort {
    int countNumber(AccountId accountId, LocalDate date);
}
