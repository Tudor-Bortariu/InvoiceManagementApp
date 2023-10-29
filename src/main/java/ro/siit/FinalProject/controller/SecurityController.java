package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import ro.siit.FinalProject.api.SecurityApi;
import ro.siit.FinalProject.config.SecurityConfig;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.UserRepository;

import java.util.UUID;

@Controller
public class SecurityController implements SecurityApi {

    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private UserRepository userRepository;

    @Override
    public String invoiceManagement(Model model) {

        return "Security/register";
    }

    @Override
    public String sendRegistrationForm (Model model,
                                        @RequestParam String username,
                                        @RequestParam("initialPassword") String initialPassword,
                                        @RequestParam("passwordCheck") String passwordCheck,
                                        @RequestParam String firstName,
                                        @RequestParam String lastName,
                                        @RequestParam String email){

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
            user.setPassword(securityConfig.passwordEncoder().encode(passwordCheck));
            user.setEmail(email);

            userRepository.saveAndFlush(user);
        }

        return "HomePage/home";
    }

    @Override
    public String logoutConfirmation (Model model){

        return "Security/logoutConfirmation";
    }
}
