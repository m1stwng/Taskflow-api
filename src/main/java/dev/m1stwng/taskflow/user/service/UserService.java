package dev.m1stwng.taskflow.user.service;

import dev.m1stwng.taskflow.user.dto.response.UserResponse;
import dev.m1stwng.taskflow.user.entity.User;
import dev.m1stwng.taskflow.user.exception.UserNotFoundException;
import dev.m1stwng.taskflow.user.mapper.UserMapper;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public UserResponse findByUserId(UUID id) {
        log.info("Finding user by id {}", id);

        final User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User with id {} not found", id);

                    return new UserNotFoundException("User with id %s not found".formatted(id));
                });

        log.info("User found");

        return userMapper.toResponse(user);
    }
}
