package ro.siit.FinalProject.service;

import com.lowagie.text.Document;
import org.springframework.ui.Model;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface InvoiceService {

    List<String> getRemainedInvoiceStatusOptions(Invoice invoice);
    List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice);
    void getInvoiceListWithCustomInputFilter(String filterParam, Model model);
    List<Invoice> getPdfInvoiceList_WithCustomInputFilter(String filter);
    void checkIfSupplierExistsForUser(Supplier supplier);
    void generatePdf(HttpServletResponse response, List<Invoice> invoiceList) throws IOException;
}
