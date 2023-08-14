package cap.s42academy.account.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.account.application.port.out.SaveAccountPort;
import cap.s42academy.account.domain.entity.Account;
import cap.s42academy.account.domain.entity.Transaction;
import cap.s42academy.account.domain.valueobject.AccountStatus;
import cap.s42academy.account.domain.valueobject.TransactionType;
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
import static cap.s42academy.account.application.service.TransferMoneyService.ACCOUNT_WITH_ID_DOES_NOT_EXISTS;
import static cap.s42academy.account.application.service.TransferMoneyService.CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransferMoneyServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private AccountProperties accountProperties;
    @SpyBean
    private SaveAccountPort saveAccountPort;
    @SpyBean
    private PrintReceiptUseCase printReceiptUseCase;

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andThereIsNoSourceAccountIdInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("sourceAccountId can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andThereIsNoTargetAccountIdInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("targetAccountId can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andThereIsNoAmountInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("amount can not be null!"));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andSourceAccountDoesNotExists() throws Exception {
        //given
        String sourceAccountId = UUID.randomUUID().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                UUID.randomUUID().toString(),
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(sourceAccountId)));
    }

    @Test
    void shouldReturnUnauthorized_whenTransferMoney_andSourceAccountHolderIsNotLoggedIn() throws Exception {
        //given
        Account sourceAccount = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                UUID.randomUUID().toString(),
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages[0]").value(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(sourceAccount.getAccountHolderId().toString())));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andMaxNumberOfTransactionPerDayIsExceeded() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                UUID.randomUUID().toString(),
                "120.00"
        );
        when(accountProperties.getMaxNumberOfTransactions()).thenReturn(0);
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andTransferMoneyAmountIsLessThanZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                UUID.randomUUID().toString(),
                "-120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andTransferMoneyAmountIsEqualZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                UUID.randomUUID().toString(),
                "0.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andBalanceAfterWithdrawalFromSourceAccountIsLessThanZero() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                UUID.randomUUID().toString(),
                "140.01"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_WITHDRAW_MONEY_INVALID_AMOUNT_OF_MONEY_TO_WITHDRAW));
    }

    @Test
    void shouldReturnBadRequest_whenTransferMoney_andTargetAccountDoesNotExist() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        String targetAccountId = UUID.randomUUID().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                targetAccountId,
                "120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(targetAccountId)));
    }

    @Test
    void shouldTransferMoney_andStoreNewBalancesInDb() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        Account targetAccount = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String targetAccountId = targetAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                targetAccountId,
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<BigDecimal> accountBalances = getAllAccounts().stream().map(Account::getBalance).toList();
        assertAll(
                () -> verify(saveAccountPort, times(2)).save(any()),
                () -> assertThat(accountBalances).containsOnly(new BigDecimal("20.00"), new BigDecimal("260.00"))
        );
    }

    @Test
    void shouldTransferMoney_andStoreNewTransactionsInTransactionHistory() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        Account targetAccount = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String targetAccountId = targetAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                targetAccountId,
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Transaction> allTransactions = getAllTransactions();
        List<TransactionType> transactionTypes = allTransactions.stream().map(Transaction::getTransactionType).toList();
        List<Boolean> isSourceAccountsTheSame = allTransactions.stream().map(Transaction::getIsSourceAccountTheSame).toList();
        assertAll(
                () -> assertThat(allTransactions).hasSize(2),
                () -> assertThat(transactionTypes).containsOnly(TransactionType.WITHDRAWAL,TransactionType.DEPOSIT),
                () -> assertThat(isSourceAccountsTheSame).containsOnly(true,false)
        );
    }

    @Test
    void shouldPrintReceipt_whenTransferIsSuccess() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(), null, user));
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        Account targetAccount = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String targetAccountId = targetAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                targetAccountId,
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        assertAll(
                () -> verify(printReceiptUseCase, times(1)).handle(any())
        );
    }

    @Test
    void shouldNotPrintReceipt_whenTransferFailed() throws Exception {
        //given
        User user = existingUser(user());
        Account sourceAccount = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String sourceAccountId = sourceAccount.getAccountId().getValue().toString();
        Account targetAccount = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("140.00")));
        String targetAccountId = targetAccount.getAccountId().getValue().toString();
        String creationRequest = """
                {
                  "sourceAccountId": "%s",
                  "targetAccountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                sourceAccountId,
                targetAccountId,
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/transfer-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isUnauthorized());
        //then
        assertAll(
                () -> verifyNoInteractions(printReceiptUseCase)
        );
    }
}
