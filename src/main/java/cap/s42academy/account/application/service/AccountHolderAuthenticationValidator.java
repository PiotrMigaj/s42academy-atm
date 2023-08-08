package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.out.ExistsOpenSessionForAccountHolderPort;
import cap.s42academy.account.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class AccountHolderAuthenticationValidator {

    static final String USER_WITH_ID_IS_UNAUTHENTICATED = "User with ID=%s is unauthenticated!";
    private final ExistsOpenSessionForAccountHolderPort existsOpenSessionForAccountHolderPort;

    @Transactional(readOnly = true)
    public void validate(Account account) {
        UUID accountHolderId = account.getAccountHolderId();
        boolean existsOpenSessionForAccountHolder = existsOpenSessionForAccountHolderPort.existsOpenSession(accountHolderId);
        if (!existsOpenSessionForAccountHolder){
            throw new IllegalStateException(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(accountHolderId));
        }
    }
}
