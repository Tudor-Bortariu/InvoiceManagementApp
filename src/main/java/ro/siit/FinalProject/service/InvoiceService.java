package ro.siit.FinalProject.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import ro.siit.FinalProject.model.Invoice;

import java.io.IOException;
import java.util.List;

public interface InvoiceService {

    List<String> getRemainedInvoiceStatusOptions(Invoice invoice);
    List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice);
    void getInvoiceListWithCustomInputFilter(String filterParam, Model model);
    List<Invoice> getPdfInvoiceList_WithCustomInputFilter(String filter);
    void checkIfSupplierExistsForUser(String supplierName);
    void generatePdf(HttpServletResponse response, List<Invoice> invoiceList) throws IOException;
}
