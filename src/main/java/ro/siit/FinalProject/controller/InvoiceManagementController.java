package ro.siit.FinalProject.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.SupplierRepository;
import ro.siit.FinalProject.service.InvoiceService;
import ro.siit.FinalProject.service.SecurityService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/invoice-management")
@RequiredArgsConstructor
public class InvoiceManagementController{
    private final InvoiceService invoiceService;
    private final SecurityService securityService;
    private final SupplierRepository supplierRepository;

    @GetMapping
    public ModelAndView getInvoiceManagementView() {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/invoiceManagement");
        modelAndView.addObject("invoices", invoiceService.findByUser_OrderByIntroductionDateDesc(securityService.getAuthenticatedUser()));
        modelAndView.addObject("suppliers", supplierRepository.findByUser_OrderBySupplierNameAsc(securityService.getAuthenticatedUser()));
        modelAndView.addObject("currentDate", LocalDate.now());
        return modelAndView;
    }

    @GetMapping("/form")
    public ModelAndView getInvoicePostForm() {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/addInvoice");
        modelAndView.addObject("supplierList", supplierRepository.findByUser_OrderBySupplierNameAsc(securityService.getAuthenticatedUser()));
        modelAndView.addObject("currentDate", LocalDate.now());
        modelAndView.addObject("minDate", LocalDate.now().minusYears(1));
        modelAndView.addObject("maxDate", LocalDate.now().plusYears(2));
        return modelAndView;
    }

    @PostMapping
    public ResponseEntity<Invoice> addInvoice(@RequestParam String invoiceNumber,
                                              @RequestParam String supplierName,
                                              @RequestParam Double value,
                                              @RequestParam String currency,
                                              @RequestParam String dueDate,
                                              @RequestParam String paymentStatus,
                                              @RequestParam MultipartFile invoiceImage) {

        User user = securityService.getAuthenticatedUser();

        if (invoiceService.findByInvoiceNumber_AndUser(invoiceNumber, user) != null) {
            throw new IllegalArgumentException("Invoice number already exists in this database. Please insert a different number for the Invoice.");
        }

        Supplier supplier = supplierRepository
                .findByUser_AndSupplierName(user, supplierName).orElseThrow(ObjectNotFoundException::new);

        Invoice addedInvoice = new Invoice();
        addedInvoice.setInvoiceNumber(invoiceNumber);
        addedInvoice.setValue(value);
        addedInvoice.setCurrency(currency);
        addedInvoice.setDueDate(LocalDate.parse(dueDate));
        addedInvoice.setStatus(paymentStatus);
        addedInvoice.setSupplier(supplier);

        if(!invoiceImage.isEmpty()){
            try {
                addedInvoice.setInvoiceImage(invoiceImage.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        addedInvoice.setUser(user);
        return new ResponseEntity<>(invoiceService.saveInvoice(addedInvoice), HttpStatus.CREATED);
    }

    @DeleteMapping("/{invoiceNumber}")
    public ResponseEntity<String> deleteInvoice(@PathVariable String invoiceNumber) {
        invoiceService.deleteByInvoiceNumber(invoiceNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{invoiceNumber}/form")
    public ModelAndView getInvoiceUpdateForm(@PathVariable String invoiceNumber) {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/editForm");
        Invoice invoice = invoiceService.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser());

        modelAndView.addObject("invoice", invoice);

        String currentSupplierName = invoice.getSupplier().getSupplierName();

        modelAndView.addObject("supplierList",
                supplierRepository.findByUser_AndSupplierName_OrderBySupplierNameAsc(securityService.getAuthenticatedUser(), currentSupplierName));
        modelAndView.addObject("availableStatus", invoiceService.getRemainedInvoiceStatusOptions(invoice));
        modelAndView.addObject("currencyList", invoiceService.getRemainedInvoiceCurrencyOptions(invoice));
        modelAndView.addObject("minDate", LocalDate.now().minusYears(1));
        modelAndView.addObject("maxDate", LocalDate.now().plusYears(2));
        return modelAndView;
    }

    @PutMapping("/{invoiceNumber}")
    public ResponseEntity<Invoice> editInvoice(@RequestParam String invoiceNumber,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam Double updatedValue,
                                    @RequestParam String updatedCurrency,
                                    @RequestParam String updatedDueDate,
                                    @RequestParam String updatedStatus,
                                    @RequestParam MultipartFile updatedInvoiceImage) {

        User user = securityService.getAuthenticatedUser();

        Invoice invoice = invoiceService.findByInvoiceNumber_AndUser(invoiceNumber, user);
        Supplier updatedSupplier = supplierRepository
                .findByUser_AndSupplierName(user, updatedSupplierName).orElseThrow(ObjectNotFoundException::new);
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
        invoiceService.saveInvoice(invoice);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @PatchMapping("/{invoiceNumber}")
    public ResponseEntity<Invoice> changePaymentStatus(@PathVariable String invoiceNumber) {
        Invoice invoice = invoiceService.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser());
        if(invoice.getStatus().equals("Not paid")) {
            invoice.setStatus("Paid");
        } else {
            invoice.setStatus("Not paid");
        }
        invoiceService.saveInvoice(invoice);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @DeleteMapping("/{invoiceNumber}/resources")
    public ResponseEntity<Invoice> deleteImageForInvoice(Model model, @PathVariable String invoiceNumber) {
        Invoice invoice = invoiceService.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser());
        invoice.setInvoiceImage(null);
        invoiceService.saveInvoice(invoice);
        return new ResponseEntity<>(invoice, HttpStatus.OK);
    }

    @GetMapping("/invoices/filter")
    public ModelAndView getFilteredInvoicesView(@RequestParam String filterParam) {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/customFilterInvoiceTable");
        invoiceService.getInvoiceListWithCustomInputFilter(filterParam, modelAndView);

        modelAndView.addObject("filterParam", filterParam);
        modelAndView.addObject("currentDate", LocalDate.now());

        return modelAndView;
    }

    @GetMapping("/{invoiceNumber}/resources")
    public @ResponseBody byte[] getInvoiceImage(Model model, @PathVariable String invoiceNumber){
        Invoice invoice = invoiceService.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser());
        if(invoice.getInvoiceImage() != null) {
                return invoice.getInvoiceImage();
            }
        throw new NullPointerException("Selected Invoice does not have a picture assigned.");
    }

    @GetMapping("/invoices/download")
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
