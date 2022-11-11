package ro.siit.FinalProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.repository.JpaSupplierRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InvoiceServiceImpl implements InvoiceService{
    @Autowired
    private JpaInvoiceRepository invoiceRepository;

    @Autowired
    private SecurityServiceImpl securityService;

    @Autowired
    private JpaSupplierRepository supplierRepository;

    @Override
    public List<String> getRemainedInvoiceStatusOptions(Invoice invoice) {
        return Stream.of("Paid", "Not paid")
                .filter(status -> !status.equals(invoice.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice) {
        return Stream.of("RON", "EUR", "USD")
                .filter(status -> !status.equals(invoice.getCurrency()))
                .collect(Collectors.toList());
    }

    @Override
    public void getInvoiceListWithCustomInputFilter(String filterParam, Model model) {
        if (filterParam.equals("Paid") || filterParam.equals("Not paid")) {
            model.addAttribute("filteredInvoiceList", invoiceRepository.findInvoiceByStatus(securityService.getUser(), filterParam));
        } else if (filterParam.equals("7") || filterParam.equals("30")) {
            model.addAttribute("filteredInvoiceList",
                    invoiceRepository.findInvoiceByDueDate(securityService.getUser(), LocalDate.now().plusDays(Integer.parseInt(filterParam)), "Not paid"));
        } else {
            model.addAttribute("invoices", invoiceRepository.findAllInvoicesByUser(securityService.getUser()));
        }
    }

    @Override
    public void checkIfSupplierExistsForUser(Supplier supplier){
        User user = securityService.getUser();
        String supplierName = supplier.getSupplierName().toUpperCase();

        if(supplierRepository.findSupplierByUserAndName(user, supplierName).isPresent()){
            throw new IllegalArgumentException("Supplier name already exists. Please insert a different name for the Supplier.");
        }
    }
}
