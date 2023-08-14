package cap.s42academy.maintenance.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.maintenance.application.port.out.SaveMaintenancePort;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static cap.s42academy.SampleTestDataFactory.maintenance;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EnableMaintenanceModeServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private SaveMaintenancePort saveMaintenancePort;

    @Test
    void shouldTakeNoAction_whenAtmIsAlreadyInMaintenanceMode() throws Exception {
        //given
        Maintenance maintenance = existingMaintenance(maintenance(MaintenanceStatus.ACTIVE, LocalDateTime.now(), null));
        //when
        mockMvc.perform(post("/api/v1/maintenance/enable-maintenance-mode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        verifyNoInteractions(saveMaintenancePort);
    }

    @Test
    void shouldEnableMaintenanceMode() throws Exception {
        //given
        //when
        mockMvc.perform(post("/api/v1/maintenance/enable-maintenance-mode")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Maintenance> allMaintenances = getAllMaintenances();
        assertAll(
                ()->verify(saveMaintenancePort,times(1)).save(any()),
                ()->assertThat(allMaintenances.get(0).getMaintenanceStatus()).isEqualTo(MaintenanceStatus.ACTIVE)
        );
    }

}
