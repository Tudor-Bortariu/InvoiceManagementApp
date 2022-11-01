package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import ro.siit.FinalProject.api.Security.SecurityApi;
import ro.siit.FinalProject.configuration.SecurityConfig;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.UserRepository;

import java.util.UUID;

@Controller
public class SecurityController implements SecurityApi {

    SecurityConfig securityConfig = new SecurityConfig();
    @Autowired
    UserRepository userRepository;

    @Override
    public String invoiceManagement(Model model) {

        return "Security/register";
    }

    @Override
    public String sendRegistrationForm (Model model,
                                        @RequestParam String username,
                                        @RequestParam("password2") String password,
                                        @RequestParam String email){

        if(userRepository.findByUsername(username) == null){
            User user = new User();
            user.setId(UUID.randomUUID());
            user.setUsername(username);
            user.setPassword(securityConfig.passwordEncoder().encode(password));
            user.setEmail(email);

            userRepository.saveAndFlush(user);
        }else{
            throw new IllegalArgumentException("Username is not available. Unique constraint on database field.");
        }

        return "HomePage/home";
    }

    @Override
    public String logoutConfirmation (Model model){

        return "Security/logoutConfirm";
    }
}
