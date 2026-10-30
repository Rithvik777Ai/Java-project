package com.tn.smarthospital.service;

import com.tn.smarthospital.dto.RegisterForm;
import com.tn.smarthospital.model.Role;
import com.tn.smarthospital.model.User;
import com.tn.smarthospital.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Transactional
    public User registerPatient(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail().toLowerCase().trim())) {
            throw new IllegalArgumentException("An account with this email address already exists.");
        }

        User user = new User(
                form.getName().trim(),
                form.getEmail().toLowerCase().trim(),
                passwordEncoder.encode(form.getPassword()),
                form.getPhone().trim(),
                Role.ROLE_PATIENT
        );
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim());
    }

    public Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return findByEmail(auth.getName());
    }

    public User createUserDirectly(String name, String email, String rawPassword, String phone, Role role) {
        User user = new User(
                name,
                email.toLowerCase().trim(),
                passwordEncoder.encode(rawPassword),
                phone,
                role
        );
        return userRepository.save(user);
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }
}

