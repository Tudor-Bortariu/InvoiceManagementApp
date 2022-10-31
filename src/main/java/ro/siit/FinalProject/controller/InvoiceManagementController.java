package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.api.Invoice.InvoiceApi;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.model.CustomUserDetails;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.repository.JpaSupplierRepository;
import ro.siit.FinalProject.service.IAuthenticationFacade;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class InvoiceManagementController implements InvoiceApi {
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Autowired
    public JpaSupplierRepository supplierRepository;

    @Autowired
    public JpaInvoiceRepository invoiceRepository;

    @Override
    public String invoiceManagement(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("invoices", invoiceRepository.findAllInvoicesByUser(authenticatedUser));
        model.addAttribute("suppliers", supplierRepository.findAllSuppliersByUser(authenticatedUser));
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/invoiceManagement";
    }

    @Override
    public String addInvoiceForm(Model model){
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("supplierList", supplierRepository.findAllSuppliersByUser(authenticatedUser));
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("minDate", LocalDate.now().minusYears(1));
        model.addAttribute("maxDate", LocalDate.now().plusYears(2));

        return "InvoiceManagement/addInvoice";
    }

    @Override
    public RedirectView addInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String supplierName,
                                   @RequestParam Double value,
                                   @RequestParam String currency,
                                   @RequestParam String dueDate,
                                   @RequestParam String paymentStatus) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        Optional<Supplier> supplier = supplierRepository.findSupplierByUserAndName(authenticatedUser, supplierName);

        Invoice addedInvoice = new Invoice(invoiceNumber, value, currency, LocalDate.parse(dueDate), paymentStatus, supplier.orElseThrow(ObjectNotFoundException::new));

        addedInvoice.setUser(((CustomUserDetails)authentication.getPrincipal()).getUser());
        invoiceRepository.saveAndFlush(addedInvoice);

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView deleteInvoice(Model model, @PathVariable String invoiceNumber) {
        invoiceRepository.deleteByInvoiceNumber(invoiceNumber);
        return new RedirectView("/invoiceManagement");
    }

    @Override
    public String editInvoiceForm(Model model, @PathVariable String invoiceNumber) {
        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumber(invoiceNumber);
        List<String> statusOptions = Arrays.asList("Paid", "Not paid");
        List<String> currencyOptions = Arrays.asList("RON", "EUR", "USD");

        model.addAttribute("invoice", invoice.orElseThrow(ObjectNotFoundException::new));

        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("supplierList",
                supplierRepository.supplierListWithoutCurrentSupplier(authenticatedUser, invoice.get().getSupplier().getSupplierName()));

        model.addAttribute("availableStatus", getRemainedInvoiceStatusOptions(invoice.get(), statusOptions));

        model.addAttribute("currencyList", getRemainedInvoiceCurrencyOptions(invoice.get(), currencyOptions));

        model.addAttribute("minDate", LocalDate.now().minusYears(1));
        model.addAttribute("maxDate", LocalDate.now().plusYears(2));

        return "InvoiceManagement/editForm";
    }

    @Override
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
        invoice.get().setDueDate(LocalDate.parse(updatedDueDate));
        invoice.get().setStatus(updatedStatus);

        invoiceRepository.saveAndFlush(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView markInvoiceAsPaid(Model model, @PathVariable String invoiceNumber) {
        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumber(invoiceNumber);

        if(invoice.orElseThrow(ObjectNotFoundException::new).getStatus().equals("Not paid")) {
            invoice.get().setStatus("Paid");
            invoiceRepository.saveAndFlush(invoice.get());
        }

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView markInvoiceAsUnpaid(Model model, @PathVariable String invoiceNumber) {
        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumber(invoiceNumber);

        if(invoice.orElseThrow(ObjectNotFoundException::new).getStatus().equals("Paid")) {
            invoice.get().setStatus("Not paid");
            invoiceRepository.saveAndFlush(invoice.get());
        }

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public String getPaidInvoicesTable(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("paidInvoices", invoiceRepository.findInvoiceByStatus(authenticatedUser,"Paid"));

        return "InvoiceManagement/paidInvoices";
    }

    @Override
    public String unpaidInvoicesTable(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("unpaidInvoices", invoiceRepository.findInvoiceByStatus(authenticatedUser,"Not paid"));
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/unpaidInvoices";
    }

    @Override
    public String getInvoicesDueNext7Days(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("dueIn7Days",
                invoiceRepository.findInvoiceByDueDate(authenticatedUser, LocalDate.now().plusDays(7), "Not paid"));
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/dueIn7Days";
    }

    @Override
    public String getInvoicesDueNext30Days(Model model) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("dueIn30Days",
                invoiceRepository.findInvoiceByDueDate(authenticatedUser, LocalDate.now().plusDays(30), "Not paid"));
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/dueIn30Days";
    }

    private List<String> getRemainedInvoiceStatusOptions(Invoice invoice, List<String> allStatusOptions) {
        return allStatusOptions
                .stream()
                .filter(status -> !status.equals(invoice.getStatus()))
                .collect(Collectors.toList());
    }

    private List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice, List<String> allCurrencyOptions) {
        return allCurrencyOptions
                .stream()
                .filter(status -> !status.equals(invoice.getCurrency()))
                .collect(Collectors.toList());
    }
}
