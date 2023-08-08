package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.FindAccountByIdPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
class FindAccountByIdAdapter implements FindAccountByIdPort {

    private final AccountRepository accountRepository;
    @Override
    public Optional<Account> findBy(AccountId accountId) {
        return accountRepository.findById(accountId);
    }
}
