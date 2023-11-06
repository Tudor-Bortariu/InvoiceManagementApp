package ro.siit.FinalProject.ServiceTest.InvoiceServiceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.service.InvoiceService;

import java.util.List;

@SpringBootTest
class InvoiceServiceTest {
    @Autowired
    private InvoiceService invoiceService;

    Invoice invoice = new Invoice();

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