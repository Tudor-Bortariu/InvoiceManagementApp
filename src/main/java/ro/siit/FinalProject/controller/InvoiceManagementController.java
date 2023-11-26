package ro.siit.FinalProject.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.mapstruct.dto.invoice.CreateInvoiceDto;
import ro.siit.FinalProject.mapstruct.dto.invoice.UpdateInvoiceDto;
import ro.siit.FinalProject.mapstruct.response.invoice.InvoiceResponse;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.service.InvoiceService;
import ro.siit.FinalProject.service.SupplierService;

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
    private final SupplierService supplierService;

    @GetMapping
    public ModelAndView getInvoiceManagementView() {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/invoiceManagement");
        modelAndView.addObject("invoices", invoiceService.findByUser_OrderByIntroductionDateDesc());
        modelAndView.addObject("suppliers", supplierService.findByUser_OrderBySupplierNameAsc());
        modelAndView.addObject("currentDate", LocalDate.now());
        return modelAndView;
    }

    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponse>> getInvoices(){
        return new ResponseEntity<>(invoiceService.findByUser_OrderByIntroductionDateDesc(), HttpStatus.OK);
    }

    @GetMapping("/form")
    public ModelAndView getCreateInvoiceForm() {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/addInvoice");
        modelAndView.addObject("supplierList", supplierService.findByUser_OrderBySupplierNameAsc());
        modelAndView.addObject("currentDate", LocalDate.now());
        modelAndView.addObject("minDate", LocalDate.now().minusYears(1));
        modelAndView.addObject("maxDate", LocalDate.now().plusYears(2));
        return modelAndView;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> addInvoice(@RequestBody CreateInvoiceDto createInvoiceDto) throws IOException {
        return new ResponseEntity<>(invoiceService.createInvoice(createInvoiceDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{invoiceNumber}")
    public ResponseEntity<String> deleteInvoice(@PathVariable Integer invoiceNumber) {
        invoiceService.deleteById(invoiceNumber);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{invoiceId}/form")
    public ModelAndView getUpdateInvoiceForm(@PathVariable Integer invoiceId) {
        ModelAndView modelAndView = new ModelAndView("InvoiceManagement/editForm");
        InvoiceResponse invoice = invoiceService.findById(invoiceId);
        modelAndView.addObject("invoice", invoice);
        modelAndView.addObject("supplierList",
                supplierService.findByUser_AndSupplierName_OrderBySupplierNameAsc(invoice.getSupplier().getSupplierName()));
        modelAndView.addObject("availableStatus", invoiceService.getRemainedInvoiceStatusOptions(invoice.getStatus()));
        modelAndView.addObject("currencyList", invoiceService.getRemainedInvoiceCurrencyOptions(invoice.getCurrency()));
        modelAndView.addObject("minDate", LocalDate.now().minusYears(1));
        modelAndView.addObject("maxDate", LocalDate.now().plusYears(2));
        return modelAndView;
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> editInvoice(@PathVariable Integer invoiceId,
                                                       @RequestBody UpdateInvoiceDto updateInvoiceDto) {
        return new ResponseEntity<>(invoiceService.updateInvoice(invoiceId, updateInvoiceDto), HttpStatus.OK);
    }

    @PatchMapping("/{invoiceNumber}")
    public ResponseEntity<InvoiceResponse> changePaymentStatus(@PathVariable String invoiceNumber) {
        return new ResponseEntity<>(invoiceService.changePaymentStatus(invoiceNumber), HttpStatus.OK);
    }

    @DeleteMapping("/{invoiceId}/resources")
    public ResponseEntity<String> deleteImageForInvoice(@PathVariable Integer invoiceId) {
        invoiceService.deleteResourceForInvoice(invoiceId);
        return new ResponseEntity<>(HttpStatus.OK);
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
    public @ResponseBody byte[] getInvoiceImage(@PathVariable String invoiceNumber){
        return invoiceService.getInvoiceImage(invoiceNumber);
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
