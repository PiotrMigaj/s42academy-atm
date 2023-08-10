package cap.s42academy.aspect.maintenance.internal;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.maintenance.domain.valueobject.MaintenanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.maintenance;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MaintenanceModeAspectIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnInternalServerError_whenAtmIsInMaintenanceMode_andThereIsHitToUserRestAdapter() throws Exception {
        //given
        existingMaintenance(maintenance(MaintenanceStatus.ACTIVE, LocalDateTime.now(), null));
        //when
        //then
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    void shouldReturnInternalServerError_whenAtmIsInMaintenanceMode_andThereIsHitToAccountRestAdapter() throws Exception {
        //given
        existingMaintenance(maintenance(MaintenanceStatus.ACTIVE, LocalDateTime.now(), null));
        //when
        //then
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                "120.00"
        );

        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isServiceUnavailable());
    }

}
