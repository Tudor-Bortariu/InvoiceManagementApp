package ro.siit.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.mapstruct.dto.user.CreateUserDto;
import ro.siit.FinalProject.mapstruct.response.user.SimpleUserResponse;
import ro.siit.FinalProject.service.UserService;

@RestController
@RequiredArgsConstructor
public class SecurityController{
    private final UserService userService;

    @GetMapping("/register/form")
    public ModelAndView getRegisterForm() {
        return new ModelAndView("Security/register");
    }

    @GetMapping("/logout-confirmation")
    public ModelAndView logoutConfirmationView(){
        return new ModelAndView("Security/logoutConfirmation");
    }

    @PostMapping("/register")
    public ResponseEntity<SimpleUserResponse> registerUser(@RequestBody CreateUserDto createUserDto){
        return new ResponseEntity<>(userService.saveUser(createUserDto), HttpStatus.CREATED);
    }
}
