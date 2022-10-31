package ro.siit.FinalProject.api.Registration;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("register")
public interface RegistrationApi {

    @GetMapping("")
    String invoiceManagement(Model model);

    @PostMapping("")
    String sendRegistrationForm (Model model,
                                        @RequestParam String username,
                                        @RequestParam("password2") String password,
                                        @RequestParam String email);

}
