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
import ro.siit.FinalProject.repository.JpaSupplierRepository;
import ro.siit.FinalProject.service.IAuthenticationFacade;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("invoiceManagement")
public class InvoiceManagementController {
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public JpaSupplierRepository supplierRepository;

    @Autowired
    public JpaInvoiceRepository invoiceRepository;

    @GetMapping("")
    public String invoiceManagement(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("invoices", invoiceRepository.findAllInvoicesByUser(authenticatedUser));

        return "InvoiceManagement/invoiceManagement";
    }

    @GetMapping("/addInvoice")
    public String addInvoiceForm(Model model){
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("supplierList", supplierRepository.findAllSuppliersByUser(authenticatedUser));

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

    @GetMapping("/delete/{invoiceNumber}")
    public RedirectView deleteInvoice(Model model, @PathVariable String invoiceNumber) {
        invoiceRepository.deleteByInvoiceNumber(invoiceNumber);
        return new RedirectView("/invoiceManagement");
    }

    @GetMapping("/edit/{invoiceNumber}")
    public String editInvoiceForm(Model model, @PathVariable String invoiceNumber) {
        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumber(invoiceNumber);
        model.addAttribute("invoice", invoice.get());

        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("supplierList", supplierRepository.findAllSuppliersByUser(authenticatedUser));

        return "InvoiceManagement/editForm";
    }

    @PostMapping("/edit")
    public RedirectView editInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String updatedSupplierName,
                                   @RequestParam Double updatedValue,
                                   @RequestParam String updatedCurrency,
                                   @RequestParam String updatedDueDate,
                                   @RequestParam String updatedStatus) {

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumber(invoiceNumber);

        invoice.get().setSupplierName(updatedSupplierName);
        invoice.get().setValue(updatedValue);
        invoice.get().setCurrency(updatedCurrency);
        invoice.get().setDueDate(updatedDueDate);
        invoice.get().setStatus(updatedStatus);

        invoiceRepository.save(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @GetMapping("/paidInvoices")
    public String getPaidInvoicesTable(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("paidInvoices", invoiceRepository.findInvoicesByStatus(authenticatedUser,"Paid"));

        return "InvoiceManagement/paidInvoices";
    }

    @GetMapping("/unpaidInvoices")
    public String unpaidInvoicesTable(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("unpaidInvoices", invoiceRepository.findInvoicesByStatus(authenticatedUser,"Not paid"));

        return "InvoiceManagement/unpaidInvoices";
    }

    @GetMapping("/dueIn7Days")
    public String getInvoicesDueNext7Days(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("dueIn7Days", invoiceRepository.findInvoicesByStatus(authenticatedUser, "Not paid")
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

        model.addAttribute("dueIn30Days", invoiceRepository.findInvoicesByStatus(authenticatedUser, "Not paid")
                .stream()
                .filter(invoice -> (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(invoice.getDueDate())) <= 30)
                        && (ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(invoice.getDueDate())) >= 0))
                .collect(Collectors.toList()));

        return "InvoiceManagement/dueIn30Days";
    }
}
