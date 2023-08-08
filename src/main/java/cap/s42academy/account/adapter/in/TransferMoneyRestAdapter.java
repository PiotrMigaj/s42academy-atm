package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.TransferMoneyCommand;
import cap.s42academy.account.application.port.in.TransferMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class TransferMoneyRestAdapter {

    private final TransferMoneyUseCase transferMoneyUseCase;

    @PostMapping("api/v1/accounts/transfer-money")
    ResponseEntity<Void> transferMoney(@RequestBody TransferMoneyCommand command){
        transferMoneyUseCase.handle(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
