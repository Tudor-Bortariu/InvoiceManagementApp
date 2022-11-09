package ro.siit.FinalProject.service;

import org.springframework.ui.Model;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;

import java.util.List;

public interface InvoiceService {

    List<String> getRemainedInvoiceStatusOptions(Invoice invoice);
    List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice);
    void getInvoiceListWithCustomInputFilter(String filterParam, Model model);
    void checkIfSupplierExistsForUser(Supplier supplier);
}
