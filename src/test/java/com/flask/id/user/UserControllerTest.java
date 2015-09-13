package com.flask.id.user;

import com.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flask.idi.user.UserDto;
import com.flask.idi.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        UserDto.Create create = createUser("flask2", "password");

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
        UserDto.Create create = createUser("flask", "");

        ResultActions result = mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        result.andDo(print());
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testUserDuplicatedException() throws Exception {
        UserDto.Create create = createUser("flask1", "password");

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
        UserDto.Create createUser = createUser("flask1", "passwrod");
        userService.createUser(createUser);
        ResultActions result = mockMvc.perform(get("/users"));

        result.andDo(print());
        result.andExpect(status().isOk());
        // {"content":[{"id":1,"username":"flask1","fullName":null,"joined":1442138512233,"updated":1442138512233}],"totalElements":1,"totalPages":1,"last":true,"size":20,"number":0,"first":true,"sort":null,"numberOfElements":1}
    }

    private UserDto.Create createUser(String username, String password) {
        UserDto.Create create = new UserDto.Create();
        create.setUsername(username);
        create.setPassword(password);
        return create;
    }
}
