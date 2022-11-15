package ro.siit.FinalProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
import ro.siit.FinalProject.api.InvoiceApi;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.repository.JpaSupplierRepository;
import ro.siit.FinalProject.service.InvoiceServiceImpl;
import ro.siit.FinalProject.service.SecurityServiceImpl;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class InvoiceManagementController implements InvoiceApi {
    @Autowired
    private InvoiceServiceImpl invoiceService;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private JpaSupplierRepository supplierRepository;

    @Autowired
    private JpaInvoiceRepository invoiceRepository;

    @Override
    public String invoiceManagement(Model model) {

        model.addAttribute("invoices", invoiceRepository.findAllInvoicesByUser(securityService.getUser()));
        model.addAttribute("suppliers", supplierRepository.findAllSuppliersByUser(securityService.getUser()));
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/invoiceManagement";
    }

    @Override
    public String addInvoiceForm(Model model) {

        model.addAttribute("supplierList", supplierRepository.findAllSuppliersByUser(securityService.getUser()));
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
                                   @RequestParam String paymentStatus,
                                   @RequestParam MultipartFile invoiceImage) {

        User user = securityService.getUser();

        if (invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, user).isPresent()) {
            throw new IllegalArgumentException("Invoice number already exists in this database. Please insert a different number for the Invoice.");
        }

        Supplier supplier = supplierRepository
                .findSupplierByUserAndName(user, supplierName).orElseThrow(ObjectNotFoundException::new);

        Invoice addedInvoice =
                new Invoice(invoiceNumber, value, currency, LocalDate.parse(dueDate), paymentStatus, supplier);

        if(!invoiceImage.isEmpty()){
            try {
                addedInvoice.setInvoiceImage(invoiceImage.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        addedInvoice.setUser(user);
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

        Invoice invoice = invoiceRepository
                .findInvoiceByNumberAndUser(invoiceNumber, securityService.getUser())
                .orElseThrow(ObjectNotFoundException::new);

        model.addAttribute("invoice", invoice);

        String currentSupplierName = invoice.getSupplier().getSupplierName();

        model.addAttribute("supplierList",
                supplierRepository.supplierListWithoutCurrentSupplier(securityService.getUser(), currentSupplierName));

        model.addAttribute("availableStatus", invoiceService.getRemainedInvoiceStatusOptions(invoice));

        model.addAttribute("currencyList", invoiceService.getRemainedInvoiceCurrencyOptions(invoice));

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
                                    @RequestParam String updatedStatus,
                                    @RequestParam MultipartFile updatedInvoiceImage) {

        User user = securityService.getUser();

        Invoice invoice = invoiceRepository
                .findInvoiceByNumberAndUser(invoiceNumber, user).orElseThrow(ObjectNotFoundException::new);

        Supplier updatedSupplier = supplierRepository
                .findSupplierByUserAndName(user, updatedSupplierName).orElseThrow(ObjectNotFoundException::new);

        invoice.setSupplier(updatedSupplier);
        invoice.setValue(updatedValue);
        invoice.setCurrency(updatedCurrency);
        invoice.setDueDate(LocalDate.parse(updatedDueDate));
        invoice.setStatus(updatedStatus);

        if(!updatedInvoiceImage.isEmpty()){
            try {
                invoice.setInvoiceImage(updatedInvoiceImage.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        invoiceRepository.saveAndFlush(invoice);

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView changePaymentStatus(Model model, @PathVariable String invoiceNumber, @RequestParam String paymentStatus) {

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, securityService.getUser());

        invoice.orElseThrow(ObjectNotFoundException::new).setStatus(paymentStatus);
        invoiceRepository.saveAndFlush(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public RedirectView deleteImageForInvoice(Model model, @PathVariable String invoiceNumber) {

        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, securityService.getUser());

        invoice.orElseThrow(ObjectNotFoundException::new).setInvoiceImage(null);

        invoiceRepository.saveAndFlush(invoice.get());

        return new RedirectView("/invoiceManagement");
    }

    @Override
    public String getFilteredInvoices(Model model, @RequestParam String filterParam) {

        invoiceService.getInvoiceListWithCustomInputFilter(filterParam, model);

        model.addAttribute("filterParam", filterParam);
        model.addAttribute("currentDate", LocalDate.now());

        return "InvoiceManagement/customFilterInvoiceTable";
    }

    @Override
    public @ResponseBody byte[] getInvoiceImage(Model model, @PathVariable String invoiceNumber){
        Optional<Invoice> invoice = invoiceRepository.findInvoiceByNumberAndUser(invoiceNumber, securityService.getUser());

        if(invoice.map(Invoice::getInvoiceImage).orElseThrow(ObjectNotFoundException::new) != null) {
                return invoice.get().getInvoiceImage();
            }

            throw new NullPointerException("Selected Invoice does not have a picture assigned.");
    }

    @Override
    public void downloadPdfFile(HttpServletResponse response, @RequestParam String filter, @RequestParam String supplierName) throws IOException {

        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=pdf_" +
                filter + "_" + LocalDateTime.now() + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<Invoice> invoiceList = invoiceService.getPdfInvoiceList_WithCustomInputFilter(filter)
                .stream()
                .filter(invoice -> invoice.getSupplier().getSupplierName().contains(supplierName.toUpperCase()))
                .collect(Collectors.toList());

        invoiceService.generatePdf(response, invoiceList);
    }
}
