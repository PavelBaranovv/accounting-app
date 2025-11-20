package spbpu.accountingapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spbpu.accountingapp.dto.Credentials;
import spbpu.accountingapp.entity.User;
import spbpu.accountingapp.enums.Role;
import spbpu.accountingapp.repository.UserRepository;

@Service
@RequiredArgsConstructor()
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User createUser(Credentials credentials) {
        String username = credentials.username();
        String password = credentials.password();
        Role role = credentials.role();

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Пользователь с таким именем уже существует");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        return userRepository.save(user);
    }
}