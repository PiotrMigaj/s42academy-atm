package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.in.RegisterUserCommand;
import cap.s42academy.user.application.port.out.ExistsUserByEmailPort;
import cap.s42academy.user.application.port.out.OpenAccountPort;
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

import java.util.UUID;

import static cap.s42academy.user.application.service.RegisterUserService.USER_WITH_EMAIL_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private SaveUserPort saveUserPort;
    @Mock
    private ExistsUserByEmailPort existsUserByEmailPort;
    @Mock
    private OpenAccountPort openAccountPort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private RegisterUserService registerUserService;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void shouldThrowException_whenUserWithEmailAlreadyExists(){
        //given
        RegisterUserCommand command = new RegisterUserCommand(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        when(existsUserByEmailPort.existsBy(any())).thenReturn(true);
        //when
        //then
        assertThatThrownBy(()->registerUserService.handle(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(USER_WITH_EMAIL_ALREADY_EXISTS.formatted(command.email()));
    }

    @Test
    void shouldSaveUser(){
        //given
        RegisterUserCommand command = new RegisterUserCommand(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        when(existsUserByEmailPort.existsBy(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(command.pin());
        //when
        registerUserService.handle(command);
        //then
        assertAll(
                ()->verify(saveUserPort,times(1)).save(userCaptor.capture()),
                ()->assertThat(userCaptor.getValue().getUserId()).isNotNull(),
                ()->assertThat(userCaptor.getValue().getFirstName()).isEqualTo(command.firstName()),
                ()->assertThat(userCaptor.getValue().getLastName()).isEqualTo(command.lastName()),
                ()->assertThat(userCaptor.getValue().getEmail()).isEqualTo(command.email()),
                ()->assertThat(userCaptor.getValue().getPin()).isEqualTo(command.pin())
        );
    }

    @Test
    void shouldOpenAccountForUser(){
        //given
        RegisterUserCommand command = new RegisterUserCommand(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        UserId userId = UserId.of(UUID.randomUUID());
        when(existsUserByEmailPort.existsBy(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(command.pin());
        when(saveUserPort.save(any())).thenReturn(userId);
        //when
        registerUserService.handle(command);
        //then
        verify(openAccountPort,times(1)).openAccountForUser(userId);
    }

    @Test
    void shouldReturnUserId(){
        //given
        RegisterUserCommand command = new RegisterUserCommand(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        UserId userId = UserId.of(UUID.randomUUID());
        when(existsUserByEmailPort.existsBy(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(command.pin());
        when(saveUserPort.save(any())).thenReturn(userId);
        //when
        UserId result = registerUserService.handle(command);
        //then
        assertThat(result).isEqualTo(userId);
    }

}
