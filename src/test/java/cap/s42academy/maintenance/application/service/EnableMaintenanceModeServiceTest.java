package cap.s42academy.maintenance.application.service;

import cap.s42academy.common.timeprovider.api.TimeProvider;
import cap.s42academy.maintenance.application.port.in.EnableMaintenanceModeCommand;
import cap.s42academy.maintenance.application.port.out.ExistsActiveMaintenanceModePort;
import cap.s42academy.maintenance.application.port.out.SaveMaintenancePort;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnableMaintenanceModeServiceTest {

    @Mock
    private ExistsActiveMaintenanceModePort existsActiveMaintenanceModePort;
    @Mock
    private TimeProvider timeProvider;
    @Mock
    private SaveMaintenancePort saveMaintenancePort;
    @InjectMocks
    private EnableMaintenanceModeService enableMaintenanceModeService;
    @Captor
    private ArgumentCaptor<Maintenance> maintenanceCaptor;

    @Test
    void shouldTakeNoAction_whenAtmIsAlreadyInMaintenanceMode(){
        //given
        when(existsActiveMaintenanceModePort.existsActive()).thenReturn(true);
        //when
        enableMaintenanceModeService.handle(new EnableMaintenanceModeCommand());
        //then
        verifyNoInteractions(saveMaintenancePort);
    }

    @Test
    void shouldEnableMaintenanceMode(){
        //given
        when(existsActiveMaintenanceModePort.existsActive()).thenReturn(false);
        //when
        enableMaintenanceModeService.handle(new EnableMaintenanceModeCommand());
        //then
        assertAll(
                ()->verify(saveMaintenancePort).save(maintenanceCaptor.capture()),
                ()->assertThat(maintenanceCaptor.getValue().getMaintenanceStatus()).isEqualTo(MaintenanceStatus.ACTIVE)
        );
    }
}
