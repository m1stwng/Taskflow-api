package dev.m1stwng.taskflow.security.service;

import dev.m1stwng.taskflow.security.entity.SecurityUser;
import dev.m1stwng.taskflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Log4j2
@NullMarked
@RequiredArgsConstructor
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public SecurityUser loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user with email {}", email);

        final SecurityUser securityUser = userRepository.findByEmail(email)
                .map(SecurityUser::of)
                .orElseThrow(() -> {
                    log.warn("User with email {} was not found", email);

                    return new UsernameNotFoundException("User with email %s was not found".formatted(email));
                });

        log.info("User loaded with id {}", securityUser.id());

        return securityUser;
    }
}
