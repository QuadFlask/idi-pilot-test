package com.flask.idi.user;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    PasswordEncoder passwordEncoder;

    public User createUser(UserDto.Create create) {
        User user = modelMapper.map(create, User.class);
        if (userRepository.findByUsername(create.getUsername()) != null) {
            log.error("user duplicated exception. {}", create.getUsername());
            throw new UserDuplicatedException();
        }

        user.setPassword(passwordEncoder.encode(create.getPassword()));
        user.setJoined(new Date());
        user.setUpdated(new Date());

        return userRepository.save(user);
    }

    public User updateUser(User user, UserDto.Update updateDto) {
        user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
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

    public void delete(Long id) {
        userRepository.delete(getUser(id));
    }
}
