package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.QueryAccountBalanceProjection;
import cap.s42academy.account.application.port.in.QueryAccountBalanceUseCase;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class QueryAccountBalanceRestAdapter {

    private final QueryAccountBalanceUseCase queryAccountBalanceUseCase;

    @GetMapping("api/v1/accounts/get-balance/{accountId}")
    QueryAccountBalanceProjection getAccountBalance(@PathVariable String accountId){
        return queryAccountBalanceUseCase.queryBy(AccountId.of(UUID.fromString(accountId)));
    }

}
