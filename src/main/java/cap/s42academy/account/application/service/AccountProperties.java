package cap.s42academy.account.application.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.math.BigDecimal;


@ConfigurationProperties(prefix = "account")
@ConstructorBinding
@Getter
@RequiredArgsConstructor
class AccountProperties {

    private final BigDecimal openingBalance;

}
