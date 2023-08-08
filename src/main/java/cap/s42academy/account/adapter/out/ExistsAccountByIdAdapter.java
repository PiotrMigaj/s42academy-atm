package cap.s42academy.account.adapter.out;

import cap.s42academy.account.application.port.out.ExistsAccountByIdPort;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class ExistsAccountByIdAdapter implements ExistsAccountByIdPort {

    private final AccountRepository accountRepository;
    @Override
    public boolean existsBy(AccountId accountId) {
        return accountRepository.existsById(accountId);
    }
}
