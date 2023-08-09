package cap.s42academy.account.application.service;

import cap.s42academy.account.application.port.in.OpenAccountCommand;
import cap.s42academy.account.application.port.in.OpenAccountUseCase;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
class OpenAccountService implements OpenAccountUseCase {

    private final SaveAccountPort saveAccountPort;
    private final AccountProperties accountProperties;

    @Transactional
    @Override
    public void handle(@Valid OpenAccountCommand command) {
        Account accountToPersist = Account.createNew(
                UUID.fromString(command.accountHolderId()),
                accountProperties.getOpeningBalance()
        );
        saveAccountPort.save(accountToPersist);
    }
}
