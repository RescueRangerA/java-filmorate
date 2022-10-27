package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void create() throws Exception {
        User user = new User(null, "mail@mail.ru", "dolore", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        this.mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(user))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@mail.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("dolore"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Nick Name"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1946-08-20"));
    }

    @Test
    void createFailLogin() throws Exception {
        User user = new User(null, "mail@mail.ru", "dolore ullamco", "Nick Name", LocalDate.parse("1946-08-20", DateTimeFormatter.ISO_DATE));

        this.mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(user))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void createFailEmail() throws Exception {
        User user = new User(null, "mail.ru", "dolore", "Nick Name", LocalDate.parse("1980-08-20", DateTimeFormatter.ISO_DATE));

        this.mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(user))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void createFailBirthday() throws Exception {
        User user = new User(null, "test@mail.ru", "dolore", "Nick Name", LocalDate.parse("2446-08-20", DateTimeFormatter.ISO_DATE));

        this.mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(user))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void update() throws Exception {
        this.mockMvc.perform(
                        put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"login\": \"doloreUpdate\",\n" +
                                        "  \"name\": \"est adipisicing\",\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"email\": \"mail@yandex.ru\",\n" +
                                        "  \"birthday\": \"1976-09-20\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("doloreUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("est adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1976-09-20"));
    }

    @Test
    void updateUnknown() throws Exception {
        this.mockMvc.perform(
                        put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"login\": \"doloreUpdate\",\n" +
                                        "  \"name\": \"est adipisicing\",\n" +
                                        "  \"id\": 9999,\n" +
                                        "  \"email\": \"mail@yandex.ru\",\n" +
                                        "  \"birthday\": \"1976-09-20\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(9999))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("mail@yandex.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("doloreUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("est adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("1976-09-20"));
    }

    @Test
    void getAll() throws Exception {
        this.mockMvc.perform(get("/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].email").value("mail@yandex.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].login").value("doloreUpdate"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("est adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].birthday").value("1976-09-20"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").doesNotExist());
    }

    @Test
    void createUserWithEmptyName() throws Exception {
        this.mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"login\": \"common\",\n" +
                                        "  \"email\": \"friend@common.ru\",\n" +
                                        "  \"birthday\": \"2000-08-20\"\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("friend@common.ru"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.login").value("common"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("common"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.birthday").value("2000-08-20"));
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
