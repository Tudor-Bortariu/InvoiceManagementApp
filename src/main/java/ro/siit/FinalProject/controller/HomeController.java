package ro.siit.FinalProject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ro.siit.FinalProject.api.Home.HomeApi;

@Controller
public class HomeController implements HomeApi {

    @Override
    public String homePage(Model model) {
        return "HomePage/home";
    }
}
