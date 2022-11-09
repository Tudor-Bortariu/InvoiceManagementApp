package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ro.siit.FinalProject.api.HomeApi;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.service.IAuthenticationFacade;

@Controller
public class HomeController implements HomeApi {

    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Override
    public String homePage(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)){
            User user = ((CustomUserDetails) authentication.getPrincipal()).getUser();

            model.addAttribute("userFirstName", user.getFirstName());
        }

        return "HomePage/home";
    }
}
