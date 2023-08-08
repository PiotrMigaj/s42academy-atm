package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.DepositMoneyCommand;
import cap.s42academy.account.application.port.in.DepositMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class DepositMoneyRestAdapter {

    private final DepositMoneyUseCase depositMoneyUseCase;

    @PostMapping("api/v1/accounts/deposit-money")
    ResponseEntity<Void> depositMoney(@RequestBody DepositMoneyCommand command){
        depositMoneyUseCase.handle(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
