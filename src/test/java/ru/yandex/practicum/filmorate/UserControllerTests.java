package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controllers.UserController;
import ru.yandex.practicum.filmorate.model.user.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserController userController;

    @AfterEach
    public void clearAll() {
        userController.clearAllUser();
    }

    @Test
    public void getUsers() throws Exception {
        User[] users = {createUser(1), createUser(2), createUser(3), createUser(4)};
        assertEquals(userController.getAllUsers().size(), 0);
        for (User user : users) {
            mockMvc.perform(post("/users")
                    .content(objectMapper.writeValueAsString(user))
                    .contentType(MediaType.APPLICATION_JSON)
            );
        }
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
        assertEquals(userController.getAllUsers().size(), 4);
        int id = userController.getAllUsers().get(2).getId();
        assertEquals(userController.getUser(id).getEmail(), "user3@mail.ru");
    }


    private int getUsersId(int index) {
        return userController.getAllUsers().size() == 0 ? 1 : userController.getAllUsers().get(index).getId();
    }

    @Test
    public void postUser() throws Exception {
        User user = createUser(10);
        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(user))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value("NameUser10"));

        mockMvc.perform(
                        post("/users")
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());
        assertEquals(userController.getAllUsers().size(), 1);

    }

    @Test
    public void putUser() throws Exception {
        User user = createUser(1);
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
        user = createUser(10);
        int id = getUsersId(0);
        user.setId(id);
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("name").value("NameUser10"));
        assertEquals(userController.getAllUsers().size(), 1);
        assertEquals(userController.getUser(id).getName(), "NameUser10");
    }

    @Test
    public void checkEmail() throws Exception {
        User user = createUser(1);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        user = createUser(2);
        user.setEmail("user2#$$mail.ru");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(userController.getAllUsers().size(), 1);
        user = createUser(3);
        user.setEmail("&user3#mail_ru");
        user.setId(1);
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        user = createUser(4);
        user.setEmail("");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(userController.getAllUsers().size(), 1);
        int id = getUsersId(0);
        assertEquals(userController.getUser(id).getEmail(), "user1@mail.ru");
        assertEquals(userController.getUser(id).getLogin(), "login1");

    }

    @Test
    public void checkLogin() throws Exception {
        User user = createUser(1);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        String[] badLogins = {"2LoginBad", "", "Koro  vart12", "Korova@", "Korova&", "ro"};
        for (int i = 0; i < badLogins.length; i++) {
            user = createUser(i + 2);
            user.setLogin(badLogins[i]);
            mockMvc.perform(post("/users")
                            .content(objectMapper.writeValueAsString(user))
                            .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }
        assertEquals(userController.getAllUsers().size(), 1);
        int id = getUsersId(0);
        assertEquals(userController.getUser(id).getLogin(), "login1");
    }

    @Test
    public void checkName() throws Exception {
        User user = createUser(1);
        user.setName("");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        int id = getUsersId(0);
        assertEquals(userController.getUser(id).getLogin(), "login1");
        assertEquals(userController.getUser(id).getName(), "login1");
    }

    @Test
    public void checkBirthday() throws Exception {
        User user = createUser(1);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        user = createUser(2);
        user.setBirthday(LocalDate.of(2030, 10, 12));
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        assertEquals(userController.getAllUsers().size(), 1);
        user = createUser(3);
        user.setId(1);
        user.setBirthday(LocalDate.of(2028, 11, 12));
        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        int id = getUsersId(0);
        assertEquals(userController.getUser(id).getBirthday(), LocalDate.of(2000, 1, 1));
    }

    private User createUser(int id) {
        return User.builder()
                .id(0)
                .login("login" + id)
                .email("user" + id + "@mail.ru")
                .name("NameUser" + id)
                .birthday(LocalDate.of(2000, 1, (id < 31 ? id : 1))).build();
    }
}
