package com.flask.idi.user;

import com.flask.idi.commons.ErrorResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper mapper;

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody @Valid UserDto.Create create, BindingResult result) {
        if (result.hasErrors()) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("bad request");
            errorResponse.setCode("bad.request");
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        User newUser = userService.createUser(create);

        return new ResponseEntity<>(mapper.map(newUser, UserDto.Response.class), HttpStatus.CREATED);
    }

    @RequestMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public PageImpl<UserDto.Response> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        List<UserDto.Response> content = page.getContent()
                .stream()
                .map(a -> mapper.map(a, UserDto.Response.class))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    @RequestMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto.Response getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return mapper.map(user, UserDto.Response.class);
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateUser(@PathVariable Long id, @RequestBody @Valid UserDto.Update updateDto, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        User user = userService.getUser(id);
        User updateUser = userService.updateUser(user, updateDto);
        return new ResponseEntity(mapper.map(updateUser, UserDto.Response.class), HttpStatus.OK);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("user not found");
        errorResponse.setCode("not.found.user.exception");
        return errorResponse;
    }

    @ExceptionHandler(UserDuplicatedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserDuplicatedException(UserDuplicatedException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("username is duplicated");
        errorResponse.setCode("duplicated.username.exception");
        return errorResponse;
    }
}

