package com.example.security_learning.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.security_learning.entity.User;
import com.example.security_learning.repository.UserRepository;

/**
 * Loads application users for Spring Security authentication.
 *
 * <p>User roles are converted to {@code ROLE_} authorities, while
 * comma-separated permissions are added as individual authorities.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user and converts their role and permissions into
     * Spring Security authorities.
     *
     * @param username the username used for authentication
     * @return the Spring Security user details
     * @throws UsernameNotFoundException if the user does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        List<String> authorities = new ArrayList<>();

        if (user.getRole() != null && !user.getRole().isBlank()) {
            authorities.add("ROLE_" + user.getRole());
        }

        if (user.getPermissions() != null
                && !user.getPermissions().isBlank()) {

            Arrays.stream(user.getPermissions().split(","))
                    .map(String::trim)
                    .filter(permission -> !permission.isBlank())
                    .forEach(authorities::add);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        authorities.stream()
                                .map(SimpleGrantedAuthority::new)
                                .toArray(SimpleGrantedAuthority[]::new)
                )
                .build();
    }
}