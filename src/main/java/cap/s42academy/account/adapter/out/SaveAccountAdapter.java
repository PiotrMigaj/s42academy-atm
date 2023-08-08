package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class SaveAccountAdapter implements SaveAccountPort {

    private final AccountRepository accountRepository;

    @Override
    public AccountId save(Account account) {
        return accountRepository.save(account).getAccountId();
    }
}
