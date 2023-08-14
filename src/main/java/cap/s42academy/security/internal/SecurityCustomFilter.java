package cap.s42academy.security.internal;

import cap.s42academy.common.exception.api.ErrorDto;
import cap.s42academy.common.exception.api.UserUnauthenticatedException;
import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.user.application.port.in.ExistsOpenSessionForUserUseCase;
import cap.s42academy.user.application.port.in.ExistsUserByIdUseCase;
import cap.s42academy.user.domain.valueobject.UserId;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
class SecurityCustomFilter extends OncePerRequestFilter {

    static final String USER_ID_HEADER_NAME = "userId";
    static final String THERE_IS_NO_REQUEST_HEADER_WITH_USER_ID = "There is no request header with userId!";
    static final String USER_WITH_ID_IS_UNAUTHENTICATED = "User with ID=%s is unauthenticated!";
    public static final String THERE_IS_NO_USER_WITH_ID = "There is no user with ID=%s";
    private final ExistsOpenSessionForUserUseCase existsOpenSessionForUserUseCase;
    private final ExistsUserByIdUseCase existsUserByIdUseCase;
    private final ObjectMapper objectMapper;
    private final TimeProvider timeProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String userId = request.getHeader(USER_ID_HEADER_NAME);
            if (userId == null) {
                throw new UserUnauthenticatedException(THERE_IS_NO_REQUEST_HEADER_WITH_USER_ID);
            }
            boolean existsUserById = existsUserByIdUseCase.existsBy(UserId.of(UUID.fromString(userId)));
            if (!existsUserById) {
                throw new UserUnauthenticatedException(THERE_IS_NO_USER_WITH_ID.formatted(userId));
            }
            boolean existsOpenSession = existsOpenSessionForUserUseCase.existsOpenSession(UUID.fromString(userId));
            if (!existsOpenSession) {
                throw new UserUnauthenticatedException(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(userId));
            }
            filterChain.doFilter(request, response);
        } catch (UserUnauthenticatedException e) {
            HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
            ErrorDto errorDto = ErrorDto.builder()
                    .time(timeProvider.now())
                    .code(unauthorized.value())
                    .error(unauthorized.getReasonPhrase())
                    .messages(List.of(e.getMessage()))
                    .build();
            response.setStatus(unauthorized.value());
            objectMapper.writeValue(response.getOutputStream(),errorDto);
        }
    }
}
