package cap.s42academy;

import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.entity.Transaction;
import cap.s42academy.account.domain.valueobject.AccountId;
import cap.s42academy.account.domain.valueobject.TransactionId;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.UserId;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@SpringBootTest(classes = AtmServerApplication.class)
@AutoConfigureMockMvc
public abstract class IntegrationTestBase {


    @Autowired
    private JpaRepository<Account, AccountId> accountRepository;
    @Autowired
    private JpaRepository<Transaction, TransactionId> transactionRepository;
    @Autowired
    private JpaRepository<User, UserId> userRepository;
    @Autowired
    private JpaRepository<Session, SessionId> sessionRepository;
    @Autowired
    private JpaRepository<Maintenance, Long> maintenanceRepository;

    @AfterEach
    void cleanUpDB() {
        accountRepository.deleteAll();
        transactionRepository.deleteAll();
        sessionRepository.deleteAll();
        userRepository.deleteAll();
        maintenanceRepository.deleteAll();
    }

    protected User existingUser(User user) {
        return userRepository.save(user);
    }

    protected List<User> getAllUsers() {
        return userRepository.findAll();
    }

    protected Session existingSession(Session session) {
        return sessionRepository.save(session);
    }

    protected List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    protected Account existingAccount(Account account) {
        return accountRepository.save(account);
    }

    protected List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    protected Transaction existingTransaction(Transaction transaction) {return transactionRepository.save(transaction);}

    protected List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    protected Maintenance existingMaintenance(Maintenance maintenance) {return maintenanceRepository.save(maintenance);}

    protected List<Maintenance> getAllMaintenances() {
        return maintenanceRepository.findAll();
    }

}
