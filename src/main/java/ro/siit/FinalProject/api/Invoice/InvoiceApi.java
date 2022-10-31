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

    @GetMapping("/markAsPaid/{invoiceNumber}")
    public RedirectView markInvoiceAsPaid(Model model, @PathVariable String invoiceNumber);

    @GetMapping("/markAsUnpaid/{invoiceNumber}")
    public RedirectView markInvoiceAsUnpaid(Model model, @PathVariable String invoiceNumber);

    @GetMapping("/paidInvoices")
    public String getPaidInvoicesTable(Model model);

    @GetMapping("/unpaidInvoices")
    public String unpaidInvoicesTable(Model model);

    @GetMapping("/dueIn7Days")
    public String getInvoicesDueNext7Days(Model model);

    @GetMapping("/dueIn30Days")
    public String getInvoicesDueNext30Days(Model model);
}
