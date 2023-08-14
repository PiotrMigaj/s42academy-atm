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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.*;
import static cap.s42academy.account.application.service.AccountHolderAuthenticationValidator.USER_WITH_ID_IS_UNAUTHENTICATED;
import static cap.s42academy.account.application.service.DepositMoneyService.ACCOUNT_WITH_ID_DOES_NOT_EXISTS;
import static cap.s42academy.account.application.service.DepositMoneyService.CAN_NOT_DEPOSIT_MONEY_INVALID_AMOUNT_OF_MONEY_TO_DEPOSIT;
import static cap.s42academy.account.application.service.MaxNumberOfTransactionsValidator.MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DepositMoneyServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @SpyBean
    private AccountProperties accountProperties;
    @SpyBean
    private SaveAccountPort saveAccountPort;
    @SpyBean
    private PrintReceiptUseCase printReceiptUseCase;


    @Test
    void shouldReturnBadRequest_whenDepositMoney_andThereIsNoAccountIdInRequest() throws Exception {
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
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("accountId can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenDepositMoney_andThereIsNoAmountInRequest() throws Exception {
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
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("amount can not be null!"));
    }

    @Test
    void shouldReturnNotFound_whenDepositMoney_andAccountDoesNotExists() throws Exception {
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
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(ACCOUNT_WITH_ID_DOES_NOT_EXISTS.formatted(accountId)));
    }

    @Test
    void shouldReturnUnauthorized_whenDepositMoney_andAccountHolderIsNotLoggedIn() throws Exception {
        //given
        Account account = existingAccount(account(UUID.randomUUID(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
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
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages[0]").value(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(account.getAccountHolderId().toString())));
    }

    @Test
    void shouldReturnBadRequest_whenDepositMoney_andMaxNumberOfTransactionPerDayIsExceeded() throws Exception {
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
                "120.00"
        );
        when(accountProperties.getMaxNumberOfTransactions()).thenReturn(0);
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(MAXIMUM_NUMBER_OF_TRANSACTIONS_PER_DAY_IS_EXCEEDED));
    }

    @Test
    void shouldDepositMoney_whenLimitOfTransactionsIsReached_butTransactionStoredInDbIsMadeByAnotherUser() throws Exception {
        //given
        User user = existingUser(user());
        existingSession(session(OPEN, LocalDateTime.now(),null,user));
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
        existingTransaction(transaction(TransactionType.DEPOSIT,new BigDecimal("120.00"), LocalDate.now(), LocalTime.now(),account,false));
        String creationRequest = """
                {
                  "accountId": "%s",
                  "amount": "%s"
                }
                """.formatted(
                account.getAccountId().getValue().toString(),
                "120.00"
        );
        when(accountProperties.getMaxNumberOfTransactions()).thenReturn(1);
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequest_whenDepositMoney_andDepositAmountIsLessThanZero() throws Exception {
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
                "-120.00"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_DEPOSIT_MONEY_INVALID_AMOUNT_OF_MONEY_TO_DEPOSIT));
    }

    @Test
    void shouldReturnBadRequest_whenDepositMoney_andDepositAmountIsEqualZero() throws Exception {
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
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(CAN_NOT_DEPOSIT_MONEY_INVALID_AMOUNT_OF_MONEY_TO_DEPOSIT));
    }

    @Test
    void shouldDepositMoney_andStoreItInDb() throws Exception {
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
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Account> allAccounts = getAllAccounts();
        assertAll(
                ()->verify(saveAccountPort,times(1)).save(any()),
                ()->assertThat(allAccounts).isNotEmpty(),
                ()->assertThat(allAccounts.get(0).getBalance()).isEqualTo(new BigDecimal("240.00"))
        );
    }

    @Test
    void shouldDepositMoney_andStoreNewTransactionInTransactionHistory() throws Exception {
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
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Transaction> allTransactions = getAllTransactions();
        assertAll(
                ()->assertThat(allTransactions).isNotEmpty(),
                ()->assertThat(allTransactions.get(0).getTransactionId()).isNotNull(),
                ()->assertThat(allTransactions.get(0).getTransactionType()).isEqualTo(TransactionType.DEPOSIT),
                ()->assertThat(allTransactions.get(0).getIsSourceAccountTheSame()).isTrue()
        );
    }

    @Test
    void shouldPrintReceipt_whenDepositIsSuccess() throws Exception {
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
                "120.00"
        );
        //when
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
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
    void shouldNotPrintReceipt_whenDepositFailed() throws Exception {
        //given
        User user = existingUser(user());
        Account account = existingAccount(account(user.getUserId().getValue(), AccountStatus.ACTIVE, new BigDecimal("120.00")));
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
        mockMvc.perform(post("/api/v1/accounts/deposit-money")
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
