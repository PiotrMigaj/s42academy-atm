package cap.s42academy.account.adapter.in;

import cap.s42academy.account.application.port.in.QueryAccountTransactionsProjection;
import cap.s42academy.account.application.port.in.QueryAccountTransactionsUseCase;
import cap.s42academy.account.domain.valueobject.AccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
class QueryAccountTransactionsRestAdapter {

    private final QueryAccountTransactionsUseCase queryAccountTransactionsUseCase;

    @GetMapping("api/v1/accounts/get-transactions/{accountId}")
    List<QueryAccountTransactionsProjection> getTransactions(@PathVariable String accountId){
        return queryAccountTransactionsUseCase.queryBy(AccountId.of(UUID.fromString(accountId)));
    }
}
