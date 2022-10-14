package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.service.IAuthenticationFacade;

@Controller
public class HomeController {
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public JpaInvoiceRepository invoiceRepository;

    @GetMapping("/")
    public String homePage(Model model) {
        return "HomePage/home";
    }

    @GetMapping("/addInvoice")
    public String addInvoiceForm(Model model){
        return "InvoiceManagement/addInvoice";
    }

    @PostMapping("/addInvoice")
    public RedirectView addInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String supplierName,
                                   @RequestParam Double value,
                                   @RequestParam String currency,
                                   @RequestParam String dueDate,
                                   @RequestParam String paymentStatus) {

        Invoice addedInvoice = new Invoice(invoiceNumber, supplierName, value, currency, dueDate, paymentStatus);

        Authentication authentication = authenticationFacade.getAuthentication();

        addedInvoice.setUser(((CustomUserDetails)authentication.getPrincipal()).getUser());
        invoiceRepository.saveAndFlush(addedInvoice);

        return new RedirectView("/invoiceManagement");
    }
}
