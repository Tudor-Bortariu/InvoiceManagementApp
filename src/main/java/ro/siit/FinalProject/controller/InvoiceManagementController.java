package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        model.addAttribute("invoices", invoiceRepository.findAllInvoicesByUser(getUser()));
        model.addAttribute("suppliers", supplierRepository.findAllSuppliersByUser(getUser()));
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/invoiceManagement";
    }

    @Override
    public String addInvoiceForm(Model model) {

        model.addAttribute("supplierList", supplierRepository.findAllSuppliersByUser(getUser()));
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

        if (invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, getUser()).isPresent()) {
            throw new IllegalArgumentException("Invoice number already exists in this database. Please insert a different number for the Invoice.");
        } else {

            Supplier supplier = supplierRepository
                    .findSupplierByUserAndName(getUser(), supplierName)
                    .orElseThrow(ObjectNotFoundException::new);

            Invoice addedInvoice =
                    new Invoice(invoiceNumber, value, currency, LocalDate.parse(dueDate), paymentStatus, supplier);

            addedInvoice.setUser(getUser());
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

        Invoice invoice = invoiceRepository
                .findInvoiceByNumberAndUser(invoiceNumber, getUser())
                .orElseThrow(ObjectNotFoundException::new);

        model.addAttribute("invoice", invoice);

        String currentInvoiceSupplierName = invoice.getSupplier().getSupplierName();

        model.addAttribute("supplierList",
                supplierRepository.supplierListWithoutCurrentSupplier(getUser(), currentInvoiceSupplierName));

        model.addAttribute("availableStatus", getRemainedInvoiceStatusOptions(invoice));

        model.addAttribute("currencyList", getRemainedInvoiceCurrencyOptions(invoice));

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

        Invoice invoice = invoiceRepository
                .findInvoiceByNumberAndUser(invoiceNumber, getUser())
                .orElseThrow(ObjectNotFoundException::new);
        Supplier updatedSupplier = supplierRepository
                .findSupplierByUserAndName(getUser(), updatedSupplierName)
                .orElseThrow(ObjectNotFoundException::new);

        invoice.setSupplier(updatedSupplier);
        invoice.setValue(updatedValue);
        invoice.setCurrency(updatedCurrency);
        invoice.setDueDate(LocalDate.parse(updatedDueDate));
        invoice.setStatus(updatedStatus);

        invoiceRepository.saveAndFlush(invoice);

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView changePaymentStatus(Model model, @PathVariable String invoiceNumber, @RequestParam String paymentStatus) {

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, getUser());

        invoice.orElseThrow(ObjectNotFoundException::new).setStatus(paymentStatus);
        invoiceRepository.saveAndFlush(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public String getFilteredInvoices(Model model, @RequestParam String filterParam) {

        getInvoiceListWithCustomInputFilter(filterParam, model);

        model.addAttribute("filterParam", filterParam);
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/customFilterInvoiceTable";
    }

    private List<String> getRemainedInvoiceStatusOptions(Invoice invoice) {
        return Stream.of("Paid", "Not paid")
                .filter(status -> !status.equals(invoice.getStatus()))
                .collect(Collectors.toList());
    }

    private List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice) {
        return Stream.of("RON", "EUR", "USD")
                .filter(status -> !status.equals(invoice.getCurrency()))
                .collect(Collectors.toList());
    }

    private User getUser() {
        Authentication authentication = authenticationFacade.getAuthentication();
        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }

    private void getInvoiceListWithCustomInputFilter(String filterParam, Model model) {
        if (filterParam.equals("Paid") || filterParam.equals("Not paid")) {
            model.addAttribute("filteredInvoiceList", invoiceRepository.findInvoiceByStatus(getUser(), filterParam));
        } else if (filterParam.equals("7") || filterParam.equals("30")) {
            model.addAttribute("filteredInvoiceList",
                    invoiceRepository.findInvoiceByDueDate(getUser(), LocalDate.now().plusDays(Integer.parseInt(filterParam)), "Not paid"));
        } else {
            model.addAttribute("invoices", invoiceRepository.findAllInvoicesByUser(getUser()));
        }
    }
}
