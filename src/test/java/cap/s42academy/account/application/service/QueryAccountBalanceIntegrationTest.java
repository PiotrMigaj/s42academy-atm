package cap.s42academy.account.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.account.application.port.out.ExistsOpenSessionForAccountHolderPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.account;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QueryAccountBalanceIntegrationTest extends IntegrationTestBase {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ExistsOpenSessionForAccountHolderPort existsOpenSessionForAccountHolderPort;



    @Test
    void shouldReturnAccountBalance() throws Exception {
        //given
        when(existsOpenSessionForAccountHolderPort.existsOpenSession(any())).thenReturn(true);
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.0")));
        //when
        //then
        mockMvc.perform(get("/api/v1/accounts/get-balance/{accountId}",account.getAccountId().getValue()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(account.getAccountId().getValue().toString()))
                .andExpect(jsonPath("$.balance").value(account.getBalance()));
    }

    @Test
    void shouldReturnNotFound_whenAccountDoesNotExists() throws Exception {
        //given
        when(existsOpenSessionForAccountHolderPort.existsOpenSession(any())).thenReturn(true);
        //when
        //then
        mockMvc.perform(get("/api/v1/accounts/get-balance/{accountId}",UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequest_whenAccountStatusIsClosed() throws Exception {
        //given
        when(existsOpenSessionForAccountHolderPort.existsOpenSession(any())).thenReturn(true);
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("120.0")));
        //when
        //then
        mockMvc.perform(get("/api/v1/accounts/get-balance/{accountId}",account.getAccountId().getValue()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorized_whenAccountHolderIsNotLoggedIn() throws Exception {
        //given
        when(existsOpenSessionForAccountHolderPort.existsOpenSession(any())).thenReturn(false);
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.CLOSED, new BigDecimal("120.0")));
        //when
        //then
        mockMvc.perform(get("/api/v1/accounts/get-balance/{accountId}",account.getAccountId().getValue()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isUnauthorized());
    }
}
