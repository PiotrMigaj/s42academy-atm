package cap.s42academy.account.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.*;
import static cap.s42academy.account.application.service.AccountHolderAuthenticationValidator.USER_WITH_ID_IS_UNAUTHENTICATED;
import static cap.s42academy.account.application.service.CloseAccountService.ACCOUNT_WITH_ID_DOES_NOT_EXISTS;
import static cap.s42academy.account.application.service.CloseAccountService.ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CloseAccountServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private SaveAccountPort saveAccountPort;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Test
    void shouldReturnNotFound_whenCloseAccount_andAccountDoesNotExists() throws Exception {
        //given
        String accountId = UUID.randomUUID().toString();
        //when
        //then
        mockMvc.perform(delete("/api/v1/accounts/close-account/{accountId}",accountId).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId)));
    }

    @Test
    void shouldReturnUnauthorized_whenCloseAccount_andAccountHolderIsNotLoggedIn() throws Exception {
        //given
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        String accountHolderId = account.getAccountHolderId().toString();
        //when
        //then
        mockMvc.perform(delete("/api/v1/accounts/close-account/{accountId}", account.getAccountId().getValue().toString()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages[0]").value(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(accountHolderId)));
    }

    @Test
    void shouldReturnBadRequest_whenCloseAccount_andAccountStatusIsNotActive() throws Exception {
        //given
        User user = existingUser(user());
        Session session = existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.CLOSED, new BigDecimal("120.00")));
        //when
        //then
        mockMvc.perform(delete("/api/v1/accounts/close-account/{accountId}", account.getAccountId().getValue().toString()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(ACCOUNT_WITH_ID_DOES_NOT_HAVE_ACTIVE_STATUS.formatted(account.getAccountId().getValue().toString())));
    }

    @Test
    void shouldCloseAccount() throws Exception {
        //given
        User user = existingUser(user());
        Session session = existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        //when
        mockMvc.perform(delete("/api/v1/accounts/close-account/{accountId}", account.getAccountId().getValue().toString()).contentType(MediaType.APPLICATION_JSON))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Account> allAccounts = getAllAccounts();
        assertAll(
                ()-> verify(saveAccountPort,times(1)).save(any()),
                ()->assertThat(allAccounts).hasSize(1),
                ()->assertThat(allAccounts.get(0).getAccountStatus()).isEqualTo(AccountStatus.CLOSED)
        );
    }

}
