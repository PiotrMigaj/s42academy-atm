package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.in.QueryAccountTransactionsProjection;
import cap.s42academy.account.domain.entity.Transaction;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.TransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

interface TransactionRepository extends JpaRepository<Transaction, TransactionId> {

    int countTransactionsByDateOfTransactionAndAccount_AccountId(LocalDate date, AccountId accountId);

    List<QueryAccountTransactionsProjection> findTop5AllByAccount_AccountIdOrderByDateOfTransactionDescTimeOfTransactionDesc(AccountId accountId);

}
