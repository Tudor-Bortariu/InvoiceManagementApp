package ro.siit.FinalProject.api;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.awt.*;

@RequestMapping("invoiceManagement")
public interface InvoiceApi {

    @GetMapping("")
    String invoiceManagement(Model model);

    @GetMapping("/addInvoice")
    String addInvoiceForm(Model model);

    @PostMapping("/addInvoice")
    RedirectView addInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String supplierName,
                                   @RequestParam Double value,
                                   @RequestParam String currency,
                                   @RequestParam String dueDate,
                                   @RequestParam String paymentStatus,
                                   @RequestParam MultipartFile invoiceImage);

    @GetMapping("/delete/{invoiceNumber}")
    RedirectView deleteInvoice(Model model, @PathVariable String invoiceNumber);

    @GetMapping("/edit/{invoiceNumber}")
    String editInvoiceForm(Model model, @PathVariable String invoiceNumber);

    @PostMapping("/edit")
    RedirectView editInvoice(Model model,
                                    @RequestParam String invoiceNumber,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam Double updatedValue,
                                    @RequestParam String updatedCurrency,
                                    @RequestParam String updatedDueDate,
                                    @RequestParam String updatedStatus,
                                    @RequestParam MultipartFile updatedInvoiceImage);

    @GetMapping("/changePaymentStatus/{invoiceNumber}")
    RedirectView changePaymentStatus(Model model, @PathVariable String invoiceNumber, @RequestParam String paymentStatus);

    @GetMapping("/filteredInvoiceTable")
    String getFilteredInvoices(Model model, @RequestParam String filterParam);

    @GetMapping("/deleteImage/{invoiceNumber}")
    RedirectView deleteImageForInvoice(Model model, @PathVariable String invoiceNumber);

    @GetMapping(value = "/download/{invoiceNumber}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody byte[] getInvoiceImage(Model model, @PathVariable String invoiceNumber);
}
