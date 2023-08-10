package cap.s42academy.user.application.service;

import cap.s42academy.common.exception.api.UserUnauthenticatedException;
import cap.s42academy.user.application.port.in.UserPinChangeCommand;
import cap.s42academy.user.application.port.out.ExistsOpenSessionForUserWithIdPort;
import cap.s42academy.user.application.port.out.FindUserByIdPort;
import cap.s42academy.user.application.port.out.SaveUserPort;
import cap.s42academy.user.domain.entity.User;
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
import java.util.Optional;

import static cap.s42academy.SampleTestDataFactory.user;
import static cap.s42academy.user.application.service.LoginUserService.THERE_IS_NO_USER_WITH_ID;
import static cap.s42academy.user.application.service.UserPinChangeService.NEW_PIN_VALUE_MUST_DIFFER_FROM_THE_CURRENT_ONE;
import static cap.s42academy.user.application.service.UserPinChangeService.USER_WITH_ID_IS_UNAUTHENTICATED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserPinChangeServiceTest {

    @Mock
    private SaveUserPort saveUserPort;
    @Mock
    private FindUserByIdPort findUserByIdPort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ExistsOpenSessionForUserWithIdPort existsOpenSessionForUserWithIdPort;
    @InjectMocks
    private UserPinChangeService userPinChangeService;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void shouldThrowException_whenUserDoesNotExist(){
        //given
        User user = user();
        UserId userId = user.getUserId();
        UserPinChangeCommand userPinChangeCommand = new UserPinChangeCommand(userId.getValue().toString(),user.getPin());
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.empty());
        //when
        //then
        assertThatThrownBy(()->userPinChangeService.handle(userPinChangeCommand))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(THERE_IS_NO_USER_WITH_ID.formatted(userId.getValue()));
    }

    @Test
    void shouldThrowException_whenUserIsNotAuthenticated(){
        //given
        User user = user();
        UserId userId = user.getUserId();
        UserPinChangeCommand userPinChangeCommand = new UserPinChangeCommand(userId.getValue().toString(),user.getPin());
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.ofNullable(user));
        when(existsOpenSessionForUserWithIdPort.existsOpenSession(userId)).thenReturn(false);
        //when
        //then
        assertThatThrownBy(()->userPinChangeService.handle(userPinChangeCommand))
                .isInstanceOf(UserUnauthenticatedException.class)
                .hasMessage(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(userId.getValue()));
    }

    @Test
    void shouldThrowException_whenNewPinIsTheSameAsOldOne(){
        //given
        User user = user();
        UserId userId = user.getUserId();
        UserPinChangeCommand userPinChangeCommand = new UserPinChangeCommand(userId.getValue().toString(),user.getPin());
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.ofNullable(user));
        when(existsOpenSessionForUserWithIdPort.existsOpenSession(userId)).thenReturn(true);
        when(passwordEncoder.matches(userPinChangeCommand.pin(),user.getPin())).thenReturn(true);
        //when
        //then
        assertThatThrownBy(()->userPinChangeService.handle(userPinChangeCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(NEW_PIN_VALUE_MUST_DIFFER_FROM_THE_CURRENT_ONE);
    }

    @Test
    void shouldUpdateUsersPin(){
        //given
        User user = user();
        UserId userId = user.getUserId();
        UserPinChangeCommand userPinChangeCommand = new UserPinChangeCommand(userId.getValue().toString(),user.getPin());
        when(findUserByIdPort.findBy(userId)).thenReturn(Optional.ofNullable(user));
        when(existsOpenSessionForUserWithIdPort.existsOpenSession(userId)).thenReturn(true);
        when(passwordEncoder.matches(userPinChangeCommand.pin(),user.getPin())).thenReturn(false);
        //when
        userPinChangeService.handle(userPinChangeCommand);
        //then
        assertAll(
                ()->verify(passwordEncoder,times(1)).encode(any()),
                ()->verify(saveUserPort,times(1)).save(any())
        );
    }

}
