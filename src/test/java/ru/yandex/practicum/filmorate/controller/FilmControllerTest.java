package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(controllers = FilmController.class)
public class FilmControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    void create() throws Exception {
        Film film = new Film(null, "nisi eiusmod", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);

        this.mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(film))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").exists());
    }

    @Test
    void createFailName() throws Exception {
        Film film = new Film(null, "", "adipisicing", LocalDate.parse("1967-03-25", DateTimeFormatter.ISO_DATE), 100);

        this.mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(film))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void createFailDescription() throws Exception {
        Film film = new Film(null, "Film name", "Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.", LocalDate.parse("1900-03-25", DateTimeFormatter.ISO_DATE), 200);

        this.mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(film))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void createFailReleaseDate() throws Exception {
        Film film = new Film(null, "Name", "Description", LocalDate.parse("1890-03-25", DateTimeFormatter.ISO_DATE), 200);

        this.mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(film))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void createFailDuration() throws Exception {
        Film film = new Film(null, "Name", "Description", LocalDate.parse("1980-03-25", DateTimeFormatter.ISO_DATE), -200);

        this.mockMvc.perform(
                        post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(film))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    void update() throws Exception {
        this.mockMvc.perform(
                        put("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"id\": 1,\n" +
                                        "  \"name\": \"Film Updated\",\n" +
                                        "  \"releaseDate\": \"1989-04-17\",\n" +
                                        "  \"description\": \"New film update decription\",\n" +
                                        "  \"duration\": 190,\n" +
                                        "  \"rate\": 4\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Film Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New film update decription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1989-04-17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(190));
    }

    @Test
    void updateUnknown() throws Exception {
        this.mockMvc.perform(
                        put("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\n" +
                                        "  \"id\": 9999,\n" +
                                        "  \"name\": \"Film Updated\",\n" +
                                        "  \"releaseDate\": \"1989-04-17\",\n" +
                                        "  \"description\": \"New film update decription\",\n" +
                                        "  \"duration\": 190,\n" +
                                        "  \"rate\": 4\n" +
                                        "}")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(9999))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Film Updated"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("New film update decription"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate").value("1989-04-17"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(190));
    }

    @Test
    void getAll() throws Exception {
        this.mockMvc.perform(get("/films").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].name").value("nisi eiusmod"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].description").value("adipisicing"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].releaseDate").value("1967-03-25"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].duration").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").doesNotExist());
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
