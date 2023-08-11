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

import static cap.s42academy.SampleTestDataFactory.*;
import static cap.s42academy.user.application.service.UserPinChangeService.*;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserPinChangeServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnBadRequest_whenUserPinChange_andThereIsNoUserIdInRequest() throws Exception {
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
        mockMvc.perform(patch("/api/v1/users/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("userId can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenUserPinChange_andThereIsNoPinInRequest() throws Exception {
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
        mockMvc.perform(patch("/api/v1/users/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("PIN can not be blank!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a000","000","00000"})
    void shouldReturnBadRequest_whenUserPinChange_andPinHasWrongFormat(String pin) throws Exception {
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
        mockMvc.perform(patch("/api/v1/users/pin-change")
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
        mockMvc.perform(patch("/api/v1/users/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(THERE_IS_NO_USER_WITH_ID.formatted(userId)));
    }

    @Test
    void shouldReturnUnauthorized_whenUserIsNotLoggedIn() throws Exception {
        //given
        User user = existingUser(user());
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString(),
                user.getPin()
        );
        //when
        //then
        mockMvc.perform(patch("/api/v1/users/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.messages[0]").value(USER_WITH_ID_IS_UNAUTHENTICATED.formatted(user.getUserId().getValue().toString())));
    }

    @Test
    void shouldBadRequest_whenPinIsTheSameAsTheStoredOne() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(passwordEncoder.encode(pin)));
        Session session = existingSession(session(OPEN, LocalDateTime.now(),null,user));
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
        mockMvc.perform(patch("/api/v1/users/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value(NEW_PIN_VALUE_MUST_DIFFER_FROM_THE_CURRENT_ONE));
    }

    @Test
    void shouldUpdatePin() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(passwordEncoder.encode(pin)));
        Session session = existingSession(session(OPEN, LocalDateTime.now(),null,user));
        String newPin = "1111";
        String creationRequest = """
                {
                  "userId": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString(),
                newPin
        );
        //when
        mockMvc.perform(patch("/api/v1/users/pin-change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<User> allUsers = getAllUsers();
        assertAll(
                ()->assertThat(allUsers).isNotEmpty(),
                ()->assertThat(passwordEncoder.matches(newPin,allUsers.get(0).getPin())).isTrue()
        );
    }

}
