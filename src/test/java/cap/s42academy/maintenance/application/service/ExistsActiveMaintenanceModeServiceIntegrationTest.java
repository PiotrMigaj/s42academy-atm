package cap.s42academy.maintenance.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.maintenance.domain.entity.Maintenance;
import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static cap.s42academy.SampleTestDataFactory.maintenance;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExistsActiveMaintenanceModeServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnTrue_whenAtmIsInMaintenanceMode() throws Exception {
        //given
        Maintenance maintenance = existingMaintenance(maintenance(MaintenanceStatus.ACTIVE, LocalDateTime.now(), null));
        //when
        //then
        mockMvc.perform(get("/api/v1/maintenance/status-check").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAtmInMaintenanceMode").value(true));
    }

    @Test
    void shouldReturnTrue_whenAtmIsNotInMaintenanceMode() throws Exception {
        //given
        Maintenance maintenance = existingMaintenance(maintenance(MaintenanceStatus.FINISHED, LocalDateTime.now(), LocalDateTime.now()));
        //when
        //then
        mockMvc.perform(get("/api/v1/maintenance/status-check").contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAtmInMaintenanceMode").value(false));
    }

}
