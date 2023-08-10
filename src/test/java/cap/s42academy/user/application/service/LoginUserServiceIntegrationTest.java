package cap.s42academy.user.application.service;

import cap.s42academy.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginUserServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

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



}
