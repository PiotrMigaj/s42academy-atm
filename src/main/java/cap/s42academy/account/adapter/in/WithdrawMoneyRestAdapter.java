package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.WithdrawMoneyCommand;
import cap.s42academy.account.application.port.in.WithdrawMoneyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class WithdrawMoneyRestAdapter {

    private final WithdrawMoneyUseCase withdrawMoneyUseCase;

    @PostMapping("api/v1/accounts/withdraw-money")
    ResponseEntity<Void> withdrawMoney(@RequestBody WithdrawMoneyCommand command){
        withdrawMoneyUseCase.handle(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
