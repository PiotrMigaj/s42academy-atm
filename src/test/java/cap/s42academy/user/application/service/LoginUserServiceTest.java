package cap.s42academy.user.application.service;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.user.application.port.in.LoginUserCommand;
import cap.s42academy.user.application.port.out.FindUserByIdPort;
import cap.s42academy.user.application.port.out.GetOpenSessionForUserWithIdPort;
import cap.s42academy.user.application.port.out.SaveSessionPort;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.SessionId;
import cap.s42academy.user.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.session;
import static cap.s42academy.SampleTestDataFactory.userWithoutIdAndWithoutPin;
import static cap.s42academy.user.application.service.LoginUserService.PIN_VALUE_DOES_NOT_MATCH_THE_STORED_ONE;
import static cap.s42academy.user.application.service.LoginUserService.THERE_IS_NO_USER_WITH_ID;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUserServiceTest {

    @Mock
    private FindUserByIdPort findUserByIdPort;
    @Mock
    private SaveSessionPort saveSessionPort;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private GetOpenSessionForUserWithIdPort getOpenSessionForUserWithIdPort;
    @InjectMocks
    private LoginUserService loginUserService;
    @Captor
    private ArgumentCaptor<Session> sessionCaptor;

    @Test
    void shouldThrowException_whenUserDoesNotExist(){
        //given
        UserId userId = UserId.of(UUID.randomUUID());
        LoginUserCommand loginUserCommand = new LoginUserCommand(userId.getValue().toString(), "0000");
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->loginUserService.handle(loginUserCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(THERE_IS_NO_USER_WITH_ID.formatted(userId.getValue()));
    }

    @Test
    void shouldThrowException_whenPinDoesNotMatchTheStoredValue(){
        //given
        UserId userId = UserId.of(UUID.randomUUID());
        LoginUserCommand loginUserCommand = new LoginUserCommand(userId.getValue().toString(), "0000");
        User user = userWithoutIdAndWithoutPin(userId, "1111");
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(loginUserCommand.pin(),user.getPin())).thenReturn(false);
        when(getOpenSessionForUserWithIdPort.getOpenSession(userId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->loginUserService.handle(loginUserCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(PIN_VALUE_DOES_NOT_MATCH_THE_STORED_ONE);
    }

    @Test
    void shouldReturnSessionId_whenOpenSessionForUserAlreadyExists(){
        //given
        UserId userId = UserId.of(UUID.randomUUID());
        LoginUserCommand loginUserCommand = new LoginUserCommand(userId.getValue().toString(), "0000");
        User user = userWithoutIdAndWithoutPin(userId, loginUserCommand.pin());
        Session session = session(OPEN, LocalDateTime.now(), null, user);
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.ofNullable(user));
        when(getOpenSessionForUserWithIdPort.getOpenSession(userId)).thenReturn(Optional.ofNullable(session));
        //when
        SessionId result = loginUserService.handle(loginUserCommand);
        //then
        assertThat(result).isEqualTo(session.getSessionId());
    }

    @Test
    void shouldPersistSession_whenThereIsNoOpenSessionForUser(){
        //given
        UserId userId = UserId.of(UUID.randomUUID());
        LoginUserCommand loginUserCommand = new LoginUserCommand(userId.getValue().toString(), "0000");
        User user = userWithoutIdAndWithoutPin(userId, loginUserCommand.pin());
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(loginUserCommand.pin(),user.getPin())).thenReturn(true);
        when(getOpenSessionForUserWithIdPort.getOpenSession(userId)).thenReturn(Optional.empty());
        //when
        loginUserService.handle(loginUserCommand);
        //then
        assertAll(
                ()->verify(saveSessionPort,times(1)).save(sessionCaptor.capture()),
                ()->assertThat(sessionCaptor.getValue().getSessionId()).isNotNull(),
                ()->assertThat(sessionCaptor.getValue().getSessionStatus()).isEqualTo(OPEN),
                ()->assertThat(sessionCaptor.getValue().getUser().getUserId()).isEqualTo(userId)
        );
    }



}
