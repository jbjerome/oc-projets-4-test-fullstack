package com.openclassrooms.starterjwt.user.service;

import com.openclassrooms.starterjwt.auth.dto.SignupRequest;
import com.openclassrooms.starterjwt.user.exception.EmailAlreadyUsedException;
import com.openclassrooms.starterjwt.user.exception.UnauthorizedDeleteException;
import com.openclassrooms.starterjwt.user.exception.UserNotFoundException;
import com.openclassrooms.starterjwt.user.model.User;
import com.openclassrooms.starterjwt.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(id)));
    }

    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public void delete(Long id, String currentUserEmail) {
        User user = this.findById(id);

        if (!Objects.equals(currentUserEmail, user.getEmail())) {
            throw new UnauthorizedDeleteException();
        }

        this.userRepository.deleteById(id);
    }

    public User register(SignupRequest signUpRequest) {
        if (this.userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyUsedException(signUpRequest.getEmail());
        }

        User user = new User(
                signUpRequest.getEmail(),
                signUpRequest.getLastName(),
                signUpRequest.getFirstName(),
                this.passwordEncoder.encode(signUpRequest.getPassword()),
                false);

        return this.userRepository.save(user);
    }
}
