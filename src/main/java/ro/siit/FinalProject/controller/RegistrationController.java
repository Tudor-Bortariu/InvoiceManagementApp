package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ro.siit.FinalProject.configuration.SecurityConfig;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.UserRepository;

import java.util.UUID;

@Controller
@RequestMapping("register")
public class RegistrationController {

    SecurityConfig securityConfig = new SecurityConfig();
    @Autowired
    UserRepository userRepository;

    @GetMapping("")
    public String invoiceManagement(Model model) {

        return "Security/register";
    }

    @PostMapping("")
    public String sendRegistrationForm (Model model, @RequestParam String username, @RequestParam("password2") String password){

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(username);
        user.setPassword(securityConfig.passwordEncoder().encode(password));

        userRepository.saveAndFlush(user);

        return "HomePage/home";
    }
}
