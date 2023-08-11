package cap.s42academy.user.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.user.domain.entity.Session;
import cap.s42academy.user.domain.entity.User;
import cap.s42academy.user.domain.valueobject.SessionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.session;
import static cap.s42academy.SampleTestDataFactory.userWithoutPin;
import static cap.s42academy.user.application.service.LogoutUserService.THERE_IS_NO_USER_WITH_ID;
import static cap.s42academy.user.domain.valueobject.SessionStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutUserServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldReturnBadRequest_whenLoginUser_andThereIsNoUserIdInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  
                }
                """;
        //when
        //then
        mockMvc.perform(patch("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("userId can not be blank!"));
    }

    @Test
    void shouldReturnNotFound_whenUserDoesNotExists() throws Exception {
        //given
        String userId = UUID.randomUUID().toString();
        String creationRequest = """
                {
                  "userId": "%s"
                }
                """.formatted(
                userId
        );
        //when
        //then
        mockMvc.perform(patch("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.messages[0]").value(THERE_IS_NO_USER_WITH_ID.formatted(userId)));
    }

    @Test
    void shouldCloseSession_whenThereIsOpenSessionForUser() throws Exception {
        //given
        String pin = "0000";
        User user = existingUser(userWithoutPin(passwordEncoder.encode(pin)));
        Session session = existingSession(session(OPEN, LocalDateTime.now(), null, user));
        String creationRequest = """
                {
                  "userId": "%s"
                }
                """.formatted(
                user.getUserId().getValue().toString()
        );
        //when
        mockMvc.perform(patch("/api/v1/users/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isOk());
        //then
        List<Session> allSessions = getAllSessions();
        assertAll(
                ()->assertThat(allSessions).isNotEmpty(),
                ()->assertThat(allSessions.get(0).getSessionId()).isEqualTo(session.getSessionId()),
                ()->assertThat(allSessions.get(0).getSessionStatus()).isEqualTo(SessionStatus.CLOSED),
                ()->assertThat(allSessions.get(0).getClosedAt()).isNotNull()
        );
    }

}
