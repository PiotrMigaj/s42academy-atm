package cap.s42academy.security.internal;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.user.application.port.in.ExistsOpenSessionForUserUseCase;
import cap.s42academy.user.application.port.in.ExistsUserByIdUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SecurityConfiguration {

    @Bean
    FilterRegistrationBean<SecurityCustomFilter> securityCustomFilter(
            ExistsOpenSessionForUserUseCase existsOpenSessionForUserUseCase,
            ExistsUserByIdUseCase existsUserByIdUseCase,
            ObjectMapper objectMapper,
            TimeProvider timeProvider
    ){
        FilterRegistrationBean<SecurityCustomFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(
                new SecurityCustomFilter(
                        existsOpenSessionForUserUseCase,
                        existsUserByIdUseCase,
                        objectMapper,
                        timeProvider
                )
        );
        registrationBean.addUrlPatterns(
                "/api/v1/accounts/*",
                "/api/v1/users/logout",
                "/api/v1/users/pin-change"
        );
        return registrationBean;
    }
}
