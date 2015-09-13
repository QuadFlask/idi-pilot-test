package com.flask.idi.user;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.util.Date;

public class UserDto {
    @Data
    public static class Create {
        @NotEmpty
        @Size(min = 6)
        private String username, password;
    }

    @Data
    public static class Response {
        private Long id;
        private String username, fullName;
        private Date joined, updated;
    }

    @Data
    public static class Update {
        private String password, fullName;
    }
}
