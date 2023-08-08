package cap.s42academy.account.application.port.in;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public interface QueryAccountBalanceProjection {

    @Value("#{target.accountId.getValue().toString()}")
    String getAccountId();
    BigDecimal getBalance();

}
