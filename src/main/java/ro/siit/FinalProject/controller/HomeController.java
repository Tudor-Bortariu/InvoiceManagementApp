package ro.siit.FinalProject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.service.UserService;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController{
    private final UserService userService;

    @GetMapping
    public ModelAndView homePageView() {
        ModelAndView modelAndView = new ModelAndView("HomePage/home");
        modelAndView.addObject("user", userService.findAuthenticatedUser());
        return modelAndView;
    }
}
