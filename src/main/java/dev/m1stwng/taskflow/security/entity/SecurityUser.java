package dev.m1stwng.taskflow.security.entity;

import dev.m1stwng.taskflow.user.entity.User;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@NullMarked
public record SecurityUser(UUID id, String email, @Nullable String password) implements UserDetails {

    public static SecurityUser of(User user) {
        return new SecurityUser(user.getId(),user.getEmail(),user.getPassword());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
