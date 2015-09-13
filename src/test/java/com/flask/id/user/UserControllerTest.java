package com.flask.id.user;

import com.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flask.idi.user.User;
import com.flask.idi.user.UserDto;
import com.flask.idi.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class UserControllerTest {

    @Autowired
    WebApplicationContext wac;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserService userService;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        UserDto.Create create = createUserFixture("flask2", "password");

        ResultActions result = mockMvc
                .perform(post("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)));

        result.andDo(print());
        result.andExpect(status().isCreated());

        result.andExpect(jsonPath("$username", is("flask2")));
    }

    @Test
    public void testCreateUser_badRequest() throws Exception {
        UserDto.Create create = createUserFixture("flask", "");

        ResultActions result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testUserDuplicatedException() throws Exception {
        UserDto.Create create = createUserFixture("flask1", "password");

        ResultActions result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        result.andDo(print());
        result.andExpect(status().isCreated());

        result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUsers() throws Exception {
        UserDto.Create createUser = createUserFixture("flask1", "password");
        userService.createUser(createUser);
        ResultActions result = mockMvc.perform(get("/users"));

        result.andDo(print());
        result.andExpect(status().isOk());
    }

    @Test
    public void testGetUser() throws Exception {
        UserDto.Create createUser = createUserFixture("flask1", "password");
        User createdUser = userService.createUser(createUser);

        ResultActions result = mockMvc.perform(get("/users/" + createdUser.getId()));

        result.andDo(print());
        result.andExpect(status().isOk());
    }

    @Test
    public void testUpdateUser() throws Exception {
        UserDto.Create createUser = createUserFixture("flask1", "password");
        User createdUser = userService.createUser(createUser);

        UserDto.Update updateUser = new UserDto.Update();
        updateUser.setFullName("flask2");
        updateUser.setPassword("password2");

        ResultActions result = mockMvc.perform(put("/users/" + createdUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser))
        );

        result.andDo(print());
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.fullName", is("flask2")));
    }

    private UserDto.Create createUserFixture(String username, String password) {
        UserDto.Create create = new UserDto.Create();
        create.setUsername(username);
        create.setPassword(password);
        return create;
    }
}
