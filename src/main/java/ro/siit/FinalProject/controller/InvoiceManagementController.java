package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.repository.UserRepository;
import ro.siit.FinalProject.service.IAuthenticationFacade;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("invoiceManagement")
public class InvoiceManagementController {
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public JpaInvoiceRepository jpaInvoiceRepository;

    @GetMapping("")
    public String invoiceManagement(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("invoices", jpaInvoiceRepository.findAllInvoicesByUser(authenticatedUser));

        return "InvoiceManagement/invoiceManagement";
    }

    @GetMapping("/delete/{invoiceNumber}")
    public RedirectView deleteInvoice(Model model, @PathVariable String invoiceNumber) {
        jpaInvoiceRepository.deleteByInvoiceNumber(invoiceNumber);
        return new RedirectView("/invoiceManagement");
    }

    @GetMapping("/edit/{invoiceNumber}")
    public String editInvoiceForm(Model model, @PathVariable String invoiceNumber) {
        Optional<Invoice> invoice = jpaInvoiceRepository.findInvoiceByNumber(invoiceNumber);
        model.addAttribute("invoice", invoice.get());
        return "InvoiceManagement/editForm";
    }

    @PostMapping("/edit")
    public RedirectView addInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String updatedSupplierName,
                                   @RequestParam Double updatedValue,
                                   @RequestParam String updatedCurrency,
                                   @RequestParam String updatedDueDate,
                                   @RequestParam String updatedStatus) {

        Optional<Invoice> invoice = jpaInvoiceRepository.findInvoiceByNumber(invoiceNumber);

        invoice.get().setSupplierName(updatedSupplierName);
        invoice.get().setValue(updatedValue);
        invoice.get().setCurrency(updatedCurrency);
        invoice.get().setDueDate(updatedDueDate);
        invoice.get().setStatus(updatedStatus);

        jpaInvoiceRepository.save(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @GetMapping("/paidInvoices")
    public String getPaidInvoicesTable(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("paidInvoices", jpaInvoiceRepository.findInvoicesByStatus(authenticatedUser,"Paid"));

        return "InvoiceManagement/paidInvoices";
    }

    @GetMapping("/unpaidInvoices")
    public String unpaidInvoicesTable(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("unpaidInvoices", jpaInvoiceRepository.findInvoicesByStatus(authenticatedUser,"Not paid"));

        return "InvoiceManagement/unpaidInvoices";
    }

    @GetMapping("/dueIn7Days")
    public String getInvoicesDueNext7Days(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("dueIn7Days", jpaInvoiceRepository.findInvoicesByStatus(authenticatedUser, "Not paid")
                .stream()
                .filter(invoice -> (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(invoice.getDueDate())) <= 7)
                        && (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(invoice.getDueDate())) >= 0))
                .collect(Collectors.toList()));

        return "InvoiceManagement/dueIn7Days";
    }

    @GetMapping("/dueIn30Days")
    public String getInvoicesDueNext30Days(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("dueIn30Days", jpaInvoiceRepository.findInvoicesByStatus(authenticatedUser, "Not paid")
                .stream()
                .filter(invoice -> (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(invoice.getDueDate())) <= 30)
                        && (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(invoice.getDueDate())) >= 0))
                .collect(Collectors.toList()));

        return "InvoiceManagement/dueIn30Days";
    }
}
