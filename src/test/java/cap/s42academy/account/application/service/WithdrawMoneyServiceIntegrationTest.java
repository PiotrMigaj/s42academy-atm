package cap.s42academy.account.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.printer.application.port.in.PrintReceiptUseCase;
import cap.s42academy.user.domain.entity.User;
import org.junit.jupiter.api.Test;
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
import static cap.s42academy.account.application.service.MaxNumberOfTransactionsValidator.MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED;
import static cap.s42academy.account.application.service.WithdrawMoneyService.ACCOUNT_WITH_ID_DOES_NOT_EXISTS;
import static cap.s42academy.account.application.service.WithdrawMoneyService.CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WithdrawMoneyServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private AccountProperties accountProperties;
    @SpyBean
    private SaveAccountPort saveAccountPort;
    @SpyBean
    private PrintReceiptUseCase printReceiptUseCase;

    @Test
    void shouldReturnBadRequest_whenWithdrawMoney_andThereIsNoAccountIdInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "amount": "%s"
                }
                """.formatted(
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("accountId can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenWithdrawMoney_andThereIsNoAmountInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "accountId": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString()
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("amount can not be null!"));
    }

    @Test
    void shouldReturnNotFound_whenWithdrawMoney_andAccountDoesNotExists() throws Exception {
        //given
        String accountId = UUID.randomUUID().toString();
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                accountId,
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId)));
    }

    @Test
    void shouldReturnUnauthorized_whenWithdrawMoney_andAccountHolderIsNotLoggedIn() throws Exception {
        //given
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages[0]").value(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(account.getAccountHolderId().toString())));
    }

    @Test
    void shouldReturnBadRequest_whenWithdrawMoney_andMaxNumberOfTransactionPerDayIsExceeded() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "120.00"
        );
        when(accountProperties.getMaxNumberOfTransactions()).thenReturn(0);
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED));
    }

    @Test
    void shouldReturnBadRequest_whenWithdrawMoney_andWithdrawAmountIsLessThanZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "-100.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW));
    }

    @Test
    void shouldReturnBadRequest_whenWithdrawMoney_andWithdrawAmountIsEqualZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "0.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW));
    }

    @Test
    void shouldReturnBadRequest_whenWithdrawMoney_andBalanceAfterWithdrawalIsLessThanZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "120.01"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW));
    }

    @Test
    void shouldWithdrawMoney_andStoreNewBalanceInDb() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Account> allAccounts = getAllAccounts();
        assertAll(
                ()->verify(saveAccountPort,times(1)).save(any()),
                ()->assertThat(allAccounts).isNotEmpty(),
                ()->assertThat(allAccounts.get(0).getBalance()).isEqualTo(new BigDecimal("20.00"))
        );
    }

    @Test
    void shouldWithdrawMoney_andStoreNewBalanceInDb_whenBalanceAfterTransactionIsZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "140.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Account> allAccounts = getAllAccounts();
        assertAll(
                ()->verify(saveAccountPort,times(1)).save(any()),
                ()->assertThat(allAccounts).isNotEmpty(),
                ()->assertThat(allAccounts.get(0).getBalance()).isEqualTo(new BigDecimal("0.00"))
        );
    }

    @Test
    void shouldPrintReceipt_whenWithdrawalIsSuccess() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "100.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        assertAll(
                ()->verify(printReceiptUseCase,times(1)).handle(any())
        );
    }

    @Test
    void shouldNotPrintReceipt_whenWithdrawalFailed() throws Exception {
        //given
        User user = existingUser(user());
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/withdraw-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isUnauthorized());
        //then
        assertAll(
                ()->verifyNoInteractions(printReceiptUseCase)
        );
    }

}
