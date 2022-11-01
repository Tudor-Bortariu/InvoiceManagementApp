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

        if(invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber.toUpperCase(), authenticatedUser).isPresent()){
            throw new IllegalArgumentException("Invoice number already exists in this database. Please insert a different number for the Invoice.");
        }else {

            Optional<Supplier> supplier = supplierRepository.findSupplierByUserAndName(authenticatedUser, supplierName);

            Invoice addedInvoice =
                    new Invoice(invoiceNumber.toUpperCase(), value, currency, LocalDate.parse(dueDate), paymentStatus, supplier.orElseThrow(ObjectNotFoundException::new));

            addedInvoice.setUser(((CustomUserDetails) authentication.getPrincipal()).getUser());
            invoiceRepository.saveAndFlush(addedInvoice);
        }

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView deleteInvoice(Model model, @PathVariable String invoiceNumber) {
        invoiceRepository.deleteByInvoiceNumber(invoiceNumber);
        return new RedirectView("/invoiceManagement");
    }

    @Override
    public String editInvoiceForm(Model model, @PathVariable String invoiceNumber) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, authenticatedUser);
        List<String> statusOptions = Arrays.asList("Paid", "Not paid");
        List<String> currencyOptions = Arrays.asList("RON", "EUR", "USD");

        model.addAttribute("invoice", invoice.orElseThrow(ObjectNotFoundException::new));

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

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, authenticatedUser);
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
    public RedirectView changePaymentStatus(Model model, @PathVariable String invoiceNumber, @RequestParam String paymentStatus) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, authenticatedUser);

        invoice.orElseThrow(ObjectNotFoundException::new).setStatus(paymentStatus);
            invoiceRepository.saveAndFlush(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public String getInvoicesByPaymentStatus(Model model, @RequestParam String paymentStatus) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("customInvoiceFilter", invoiceRepository.findInvoiceByStatus(authenticatedUser,paymentStatus));
        model.addAttribute("paymentStatus", paymentStatus);
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/customFilterInvoiceManagement";
    }

    @Override
    public String getInvoicesByDueDate(Model model, @RequestParam String daysUntilDue) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User authenticatedUser = ((CustomUserDetails)authentication.getPrincipal()).getUser();

        model.addAttribute("customInvoiceFilter",
                invoiceRepository.findInvoiceByDueDate(authenticatedUser, LocalDate.now().plusDays(Integer.parseInt(daysUntilDue)), "Not paid"));
        model.addAttribute("currentDate", LocalDate.now());
        model.addAttribute("daysUntilDue", daysUntilDue);

        return "InvoiceManagement/customFilterInvoiceManagement";
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
