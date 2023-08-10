package cap.s42academy.user.application.service;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.user.application.port.in.LogoutUserCommand;
import cap.s42academy.user.application.port.out.ExistsUserByIdPort;
import cap.s42academy.user.application.port.out.GetOpenSessionForUserWithIdPort;
import cap.s42academy.user.application.port.out.SaveSessionPort;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.session;
import static cap.s42academy.SampleTestDataFactory.user;
import static cap.s42academy.user.application.service.LogoutUserService.THERE_IS_NO_USER_WITH_ID;
import static cap.s42academy.user.domain.valueobject.SessionStatus.CLOSED;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutUserServiceTest {

    @Mock
    private GetOpenSessionForUserWithIdPort getOpenSessionForUserWithIdPort;
    @Mock
    private SaveSessionPort saveSessionPort;
    @Mock
    private ExistsUserByIdPort existsUserByIdPort;
    @Mock
    private TimeProvider timeProvider;
    @InjectMocks
    private LogoutUserService logoutUserService;
    @Captor
    private ArgumentCaptor<Session> sessionCaptor;

    @Test
    void shouldThrowException_whenUserDoesNotExist(){
        //given
        UserId userId = UserId.of(UUID.randomUUID());
        LogoutUserCommand logoutUserCommand = new LogoutUserCommand(userId.getValue().toString());
        when(existsUserByIdPort.existsBy(userId)).thenReturn(false);
        //when
        //then
        assertThatThrownBy(()->logoutUserService.handle(logoutUserCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(THERE_IS_NO_USER_WITH_ID.formatted(userId.getValue()));
    }

    @Test
    void shouldTakeNoAction_whenThereIsNoOpenSessionForUserAndNoNeedToLogout(){
        //given
        UserId userId = UserId.of(UUID.randomUUID());
        LogoutUserCommand logoutUserCommand = new LogoutUserCommand(userId.getValue().toString());
        when(existsUserByIdPort.existsBy(userId)).thenReturn(true);
        when(getOpenSessionForUserWithIdPort.getOpenSession(userId)).thenReturn(Optional.empty());
        //when
        logoutUserService.handle(logoutUserCommand);
        //then
        verifyNoInteractions(saveSessionPort);
    }

    @Test
    void shouldCloseSession_whenThereIsOpenSessionForUser(){
        //given
        User user = user();
        UserId userId = user.getUserId();
        LogoutUserCommand logoutUserCommand = new LogoutUserCommand(userId.getValue().toString());
        Session session = session(OPEN, LocalDateTime.now(), null, user);
        when(existsUserByIdPort.existsBy(userId)).thenReturn(true);
        when(getOpenSessionForUserWithIdPort.getOpenSession(userId)).thenReturn(Optional.ofNullable(session));
        //when
        logoutUserService.handle(logoutUserCommand);
        //then
        assertAll(
                ()->verify(saveSessionPort,times(1)).save(sessionCaptor.capture()),
                ()->assertThat(sessionCaptor.getValue().getSessionId()).isEqualTo(session.getSessionId()),
                ()->assertThat(sessionCaptor.getValue().getSessionStatus()).isEqualTo(CLOSED),
                ()->assertThat(sessionCaptor.getValue().getUser().getUserId()).isEqualTo(userId)
        );
    }

}
