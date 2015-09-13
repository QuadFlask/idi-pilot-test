package com.flask.id.user;

import com.flask.idi.user.User;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserTest {

    @Test
    public void getterSetter() {
        User user = new User();
        user.setUsername("flask");
        user.setPassword("abcd");

        assertThat(user.getUsername(), is("flask"));
        assertThat(user.getPassword(), is("abcd"));
    }
}
