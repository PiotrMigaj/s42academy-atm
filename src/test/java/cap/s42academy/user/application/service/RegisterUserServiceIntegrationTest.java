package cap.s42academy.user.application.service;

import cap.s42academy.IntegrationTestBase;
import cap.s42academy.user.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static cap.s42academy.SampleTestDataFactory.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class RegisterUserServiceIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnBadRequest_whenRegisterUser_andThereIsNoFirstNameInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "lastName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("firstName can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterUser_andThereIsNoLastNameInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "firstName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("lastName can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterUser_andThereIsNoEmailInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("email can not be blank!"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterUser_andThereIsNoPinInRequest() throws Exception {
        //given
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString()
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("PIN can not be blank!"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a000","000","00000"})
    void shouldReturnBadRequest_whenRegisterUser_andPinHasWrongFormat(String pin) throws Exception {
        //given
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                pin
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("PIN must contain 4 digits!"));
    }

    @Test
    void shouldReturnBadRequest_whenRegisterUser_andUserWithSuchAndEmailExists() throws Exception {
        //given
        User user = existingUser(user());
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                user.getEmail(),
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("User with email: %s already exists!".formatted(user.getEmail())));
    }

    @Test
    void shouldCreateUser() throws Exception {
        //given
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "0000"
        );
        //when
        //then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty());
    }

    @Test
    void shouldPersistUser() throws Exception {
        //given
        User user = user();
        String creationRequest = """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "email": "%s",
                  "pin": "%s"
                }
                """.formatted(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPin()
        );
        //when
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(creationRequest))
                .andDo(log())
                .andExpect(status().isCreated());
        //then
        List<User> allUsers = getAllUsers();
        assertAll(
                ()->assertThat(allUsers).isNotEmpty(),
                ()->assertThat(allUsers.get(0).getUserId()).isNotNull(),
                ()->assertThat(allUsers.get(0).getFirstName()).isEqualTo(user.getFirstName()),
                ()->assertThat(allUsers.get(0).getLastName()).isEqualTo(user.getLastName()),
                ()->assertThat(allUsers.get(0).getEmail()).isEqualTo(user.getEmail())
        );
    }

}
