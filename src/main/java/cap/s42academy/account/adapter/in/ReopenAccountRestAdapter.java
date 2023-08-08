package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.ReopenAccountUseCase;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class ReopenAccountRestAdapter {

    private final ReopenAccountUseCase reopenAccountUseCase;

    @PatchMapping("api/v1/accounts/reopen-account/{accountId}")
    ResponseEntity<Void> closeAccount(@PathVariable String accountId){
        reopenAccountUseCase.handle(AccountId.of(UUID.fromString(accountId)));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
