package cap.s42academy.user.application.service;

import cap.s42academy.user.application.port.out.ExistsOpenSessionForUserWithIdPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistsOpenSessionForUserServiceTest {

    @Mock
    private ExistsOpenSessionForUserWithIdPort existsOpenSessionForUserWithIdPort;

    @InjectMocks
    private ExistsOpenSessionForUserService existsOpenSessionForUserService;

    @Test
    void shouldReturnTrue_whenOpenSessionExists(){
        //given
        when(existsOpenSessionForUserWithIdPort.existsOpenSession(any())).thenReturn(true);
        //when
        boolean result = existsOpenSessionForUserService.existsOpenSession(any());
        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalse_whenOpenSessionDoesNotExist(){
        //given
        when(existsOpenSessionForUserWithIdPort.existsOpenSession(any())).thenReturn(false);
        //when
        boolean result = existsOpenSessionForUserService.existsOpenSession(any());
        //then
        assertThat(result).isFalse();
    }

}
