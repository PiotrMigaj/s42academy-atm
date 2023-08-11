package cap.s42academy.user.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.session;
import static cap.s42academy.SampleTestDataFactory.userWithoutPin;
import static cap.s42academy.user.application.service.LoginUserService.PIN_VALUE_DOES_NOT_MATCH_THE_STORED_ONE;
import static cap.s42academy.user.application.service.LoginUserService.THERE_IS_NO_USER_WITH_ID;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginUserServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnBadRequest_whenLoginUser_andThereIsNoUserIdRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "pin": "%s"
                }
                """.formatted(
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("userId can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenLoginUser_andThereIsNoPinInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "userId": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString()
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("PIN can not be blank!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a000","000","00000"})
    void shouldReturnBadRequest_whenLoginUser_andPinHasWrongFormat(String pin) throws Exception {
        //given
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                pin
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("PIN must contain 4 digits!"));
    }

    @Test
    void shouldReturnNotFound_whenUserDoesNotExists() throws Exception {
        //given
        String userId = UUID.randomUUID().toString();
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                userId,
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(THERE_IS_NO_USER_WITH_ID.formatted(userId)));
    }

    @Test
    void shouldReturnBadRequest_whenPinDoesNotMatchTheStoredValue() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(pin));
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString(),
                "1111"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(PIN_VALUE_DOES_NOT_MATCH_THE_STORED_ONE));
    }

    @Test
    void shouldReturnSessionId_whenOpenSessionExistsForUser() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(passwordEncoder.encode(pin)));
        Session session = existingSession(session(OPEN, LocalDateTime.now(), null, user));
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString(),
                pin
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(session.getSessionId().getValue().toString()));
    }

    @Test
    void shouldCreateSession() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(passwordEncoder.encode(pin)));
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString(),
                pin
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").isNotEmpty());
    }

    @Test
    void shouldPersistSession() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(passwordEncoder.encode(pin)));
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString(),
                pin
        );
        //when
        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Session> allSessions = getAllSessions();
        assertAll(
                ()->assertThat(allSessions).isNotEmpty(),
                ()->assertThat(allSessions.get(0).getSessionId()).isNotNull(),
                ()->assertThat(allSessions.get(0).getSessionStatus()).isEqualTo(OPEN),
                ()->assertThat(allSessions.get(0).getUser().getUserId()).isEqualTo(user.getUserId())
        );
    }



}
