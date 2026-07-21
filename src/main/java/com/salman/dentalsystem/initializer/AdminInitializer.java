package com.salman.dentalsystem.initializer;

import com.salman.dentalsystem.model.entity.User;
import com.salman.dentalsystem.model.enums.EntityStatus;
import com.salman.dentalsystem.model.enums.Role;
import com.salman.dentalsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.admin.username}")
    private String username;

    @Value("${spring.admin.password}")
    private String password;

    @Override
    public void run(String @NonNull ... args) {
        if (!userRepository.existsByUsername(username)) {
            User admin = new User();
            admin.setName("Salman");
            admin.setSurname("Salmanov");
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setPatronymic("Samir");
            admin.setPhoneNumber("+994557103510");
            admin.setStatus(EntityStatus.ACTIVE);
            admin.setRole(Role.ADMIN);
            admin.setXrayAgent(null);
            admin.setDeletedAt(null);

            userRepository.save(admin);
            log.info("Default ADMIN istifadəçisi uğurla yaradıldı.");
        } else {
            log.info("ADMIN istifadəçisi artıq bazada mövcuddur.");
        }
    }
}
