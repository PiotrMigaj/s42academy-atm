package cap.s42academy.account.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.account.application.port.out.ExistsOpenSessionForAccountHolderPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.entity.Transaction;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.account.domain.valueobject.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.account;
import static cap.s42academy.SampleTestDataFactory.transaction;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QueryAccountTransactionsServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExistsOpenSessionForAccountHolderPort existsOpenSessionForAccountHolderPort;

    @Test
    void shouldReturnNotFound_whenAccountDoesNotExists() throws Exception {
        //given
        when(existsOpenSessionForAccountHolderPort.existsOpenSession(any())).thenReturn(true);
        //when
        //then
        mockMvc.perform(get("/api/v1/accounts/get-transactions/{accountId}", UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(get("/api/v1/accounts/get-transactions/{accountId}",account.getAccountId().getValue()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnTransactionsReport() throws Exception {
        //given
        when(existsOpenSessionForAccountHolderPort.existsOpenSession(any())).thenReturn(true);
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.0")));
        Transaction tr1 = existingTransaction(transaction(TransactionType.DEPOSIT, new BigDecimal("150.00"), LocalDate.of(2022, 1, 1), LocalTime.of(8, 30), account, true));
        Transaction tr2 = existingTransaction(transaction(TransactionType.DEPOSIT, new BigDecimal("150.00"), LocalDate.of(2022, 1, 1), LocalTime.of(8, 40), account, true));
        Transaction tr3 = existingTransaction(transaction(TransactionType.DEPOSIT, new BigDecimal("150.00"), LocalDate.of(2022, 1, 1), LocalTime.of(8, 45), account, true));
        Transaction tr4 = existingTransaction(transaction(TransactionType.DEPOSIT, new BigDecimal("150.00"), LocalDate.of(2023, 1, 1), LocalTime.of(8, 30), account, true));
        Transaction tr5 = existingTransaction(transaction(TransactionType.DEPOSIT, new BigDecimal("150.00"), LocalDate.of(2023, 1, 1), LocalTime.of(8, 40), account, true));
        Transaction tr6 = existingTransaction(transaction(TransactionType.DEPOSIT, new BigDecimal("150.00"), LocalDate.of(2023, 1, 1), LocalTime.of(8, 45), account, true));
        //when
        //then
        mockMvc.perform(get("/api/v1/accounts/get-transactions/{accountId}",account.getAccountId().getValue()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].transactionId").value(tr6.getTransactionId().getValue().toString()))
                .andExpect(jsonPath("$[1].transactionId").value(tr5.getTransactionId().getValue().toString()))
                .andExpect(jsonPath("$[2].transactionId").value(tr4.getTransactionId().getValue().toString()))
                .andExpect(jsonPath("$[3].transactionId").value(tr3.getTransactionId().getValue().toString()))
                .andExpect(jsonPath("$[4].transactionId").value(tr2.getTransactionId().getValue().toString()))
                .andExpect(jsonPath("$[5].transactionId").doesNotExist());
    }
}
