package ro.siit.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.service.UserService;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class SecurityController{
    private final UserService userService;

    @GetMapping("/register/form")
    public ModelAndView getRegisterForm() {
        return new ModelAndView("Security/register");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String username,
                                               @RequestParam("initialPassword") String initialPassword,
                                               @RequestParam("passwordCheck") String passwordCheck,
                                               @RequestParam String firstName,
                                               @RequestParam String lastName,
                                               @RequestParam String email){

        userService.saveUser(username, initialPassword, passwordCheck, firstName, lastName, email);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/logout-confirmation")
    public ModelAndView logoutConfirmationView(){
        return new ModelAndView("Security/logoutConfirmation");
    }
}
