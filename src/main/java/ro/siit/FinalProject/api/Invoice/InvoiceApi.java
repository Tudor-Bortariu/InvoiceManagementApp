package ro.siit.FinalProject.api.Invoice;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RequestMapping("invoiceManagement")
public interface InvoiceApi {

    @GetMapping("")
    public String invoiceManagement(Model model);

    @GetMapping("/addInvoice")
    String addInvoiceForm(Model model);

    @PostMapping("invoiceManagement/addInvoice")
    public RedirectView addInvoice(Model model,
                                   @RequestParam String invoiceNumber,
                                   @RequestParam String supplierName,
                                   @RequestParam Double value,
                                   @RequestParam String currency,
                                   @RequestParam String dueDate,
                                   @RequestParam String paymentStatus);

    @GetMapping("/delete/{invoiceNumber}")
    public RedirectView deleteInvoice(Model model, @PathVariable String invoiceNumber);

    @GetMapping("/edit/{invoiceNumber}")
    public String editInvoiceForm(Model model, @PathVariable String invoiceNumber);

    @PostMapping("/edit")
    public RedirectView editInvoice(Model model,
                                    @RequestParam String invoiceNumber,
                                    @RequestParam String updatedSupplierName,
                                    @RequestParam Double updatedValue,
                                    @RequestParam String updatedCurrency,
                                    @RequestParam String updatedDueDate,
                                    @RequestParam String updatedStatus);

    @GetMapping("/changePaymentStatus/{invoiceNumber}")
    public RedirectView changePaymentStatus(Model model, @PathVariable String invoiceNumber, @RequestParam String paymentStatus);

    @GetMapping("/filteredInvoiceTable")
    public String getFilteredInvoices(Model model, @RequestParam String filterParam);
}
