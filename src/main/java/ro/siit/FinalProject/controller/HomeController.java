package ro.siit.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.service.SecurityService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController{
    private final SecurityService securityService;

    @GetMapping
    public ModelAndView homePage() {
        ModelAndView modelAndView = new ModelAndView("HomePage/home");
        //TODO (TUDOR): Insert Short User DTO for FE info on homepage
        if(securityService.isAuthenticated()) {
            modelAndView.addObject("userFirstName", securityService.getAuthenticatedUser().getFirstName());
        }
        return modelAndView;
    }
}
