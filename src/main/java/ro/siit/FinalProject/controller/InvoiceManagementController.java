package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.repository.JpaSupplierRepository;
import ro.siit.FinalProject.service.IAuthenticationFacade;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
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
        model.addAttribute("suppliers", supplierRepository.findAllSuppliersByUser(authenticatedUser));

        return "InvoiceManagement/invoiceManagement";
    }

    @GetMapping("/addInvoice")
    public String addInvoiceForm(Model model){
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("supplierList", supplierRepository.findAllSuppliersByUser(authenticatedUser));
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("minDate", LocalDate.now().minusYears(1));
        model.addAttribute("maxDate", LocalDate.now().plusYears(2));

        return "InvoiceManagement/addInvoice";
    }

    @PostMapping("invoiceManagement/addInvoice")
    public RedirectView addInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String supplierName,
                                   @RequestParam Double value,
                                   @RequestParam String currency,
                                   @RequestParam String dueDate,
                                   @RequestParam String paymentStatus) {

        Authentication authentication = authenticationFacade.getAuthentication();
        Optional<Supplier> supplier = supplierRepository.findSupplierByUserAndName(((CustomUserDetails)authentication.getPrincipal()).getUser(), supplierName);

        Invoice addedInvoice = new Invoice(invoiceNumber, value, currency, dueDate, paymentStatus, supplier.orElseThrow(ObjectNotFoundException::new));

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
        List<String> statusOptions = Arrays.asList("Paid", "Not paid");
        List<String> currencyOptions = Arrays.asList("RON", "EUR", "USD");

        model.addAttribute("invoice", invoice.orElseThrow(ObjectNotFoundException::new));

        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("supplierList",
                supplierRepository.supplierListWithoutCurrentSupplier(authenticatedUser, invoice.get().getSupplier().getSupplierName()));

        model.addAttribute("availableStatus", statusOptions
                .stream()
                .filter(status -> !status.equals(invoice.get().getStatus()))
                .collect(Collectors.toList()));

        model.addAttribute("currencyList", currencyOptions.stream()
                .filter(currency -> !currency.equals(invoice.get().getCurrency()))
                .collect(Collectors.toList()));

        model.addAttribute("minDate", LocalDate.now().minusYears(1));
        model.addAttribute("maxDate", LocalDate.now().plusYears(2));

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

        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumber(invoiceNumber);
        Optional<Supplier> updatedSupplier = supplierRepository.findSupplierByUserAndName(authenticatedUser, updatedSupplierName);

        invoice.orElseThrow(ObjectNotFoundException::new).setSupplier(updatedSupplier.orElseThrow(ObjectNotFoundException::new));
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
