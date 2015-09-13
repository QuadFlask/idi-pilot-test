package com.flask.idi.user;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class UserService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ModelMapper modelMapper;

    public User createUser(UserDto.Create create) {
        User user = modelMapper.map(create, User.class);
        user.setJoined(new Date());
        user.setUpdated(new Date());
        if (userRepository.findByUsername(user.getUsername()) != null) {
            log.error("user duplicated exception. {}", create.getUsername());
            throw new UserDuplicatedException();
        }

        return userRepository.save(user);
    }

    public User updateUser(User user, UserDto.Update updateDto) {
        user.setFullName(updateDto.getFullName());
        user.setPassword(updateDto.getPassword());
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        User user = userRepository.findOne(id);
        if (user == null)
            throw new UserNotFoundException(id);
        return user;
    }
}
