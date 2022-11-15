package ro.siit.FinalProject.ServiceTest.InvoiceServiceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.service.InvoiceServiceImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class InvoiceServiceImplTest {
    @Autowired
    private InvoiceServiceImpl invoiceService;

    Invoice invoice = new Invoice("invoiceNumber", 200.0, "RON", LocalDate.now(), "Paid", new Supplier());

    @Test
    public void getRemainedInvoice_StatusOptionsTest(){
        List<String> remainedStatusOptions = invoiceService.getRemainedInvoiceStatusOptions(invoice);

        Assertions.assertEquals(remainedStatusOptions.size(), 1);
        Assertions.assertEquals(remainedStatusOptions.get(0), "Not paid");
    }

    @Test
    public void getRemainedInvoice_CurrencyOptionsTest(){
        List<String> remainedCurrencyOptions = invoiceService.getRemainedInvoiceCurrencyOptions(invoice);

        Assertions.assertEquals(remainedCurrencyOptions.size(), 2);
        Assertions.assertEquals(remainedCurrencyOptions.get(0), "EUR");
        Assertions.assertEquals(remainedCurrencyOptions.get(1), "USD");
    }
}