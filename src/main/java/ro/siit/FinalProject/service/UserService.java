package ro.siit.FinalProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.UserRepository;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public void saveUser(String username, String initialPassword, String passwordCheck, String firstName, String lastName, String email){
        if(userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username is not available. Please insert a different Username.");
        } else {
            if(!initialPassword.equals(passwordCheck)){
                throw new IllegalArgumentException("Passwords are not matching. Please make sure you complete the passwords correctly.");
            }
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername(username);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(encoder.encode(passwordCheck));
            user.setEmail(email);
            userRepository.saveAndFlush(user);
        }
    }
}
