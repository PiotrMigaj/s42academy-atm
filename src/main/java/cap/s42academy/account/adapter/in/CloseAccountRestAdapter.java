package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.CloseAccountUseCase;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class CloseAccountRestAdapter {

    private final CloseAccountUseCase closeAccountUseCase;

    @DeleteMapping("api/v1/accounts/close-account/{accountId}")
    ResponseEntity<Void> closeAccount(@PathVariable String accountId){
        closeAccountUseCase.handle(AccountId.of(UUID.fromString(accountId)));
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
