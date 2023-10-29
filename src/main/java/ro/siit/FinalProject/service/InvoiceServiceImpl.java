package ro.siit.FinalProject.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.JpaInvoiceRepository;
import ro.siit.FinalProject.repository.JpaSupplierRepository;

import java.io.IOException;
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
    public List<Invoice> getPdfInvoiceList_WithCustomInputFilter(String filter) {
        if (filter.equals("Paid") || filter.equals("Not paid")) {
            return invoiceRepository.findInvoiceByStatus(securityService.getUser(), filter);
        } else if (filter.equals("7") || filter.equals("30")) {
            return invoiceRepository.findInvoiceByDueDate(securityService.getUser(), LocalDate.now().plusDays(Integer.parseInt(filter)), "Not paid");
        } else {
            return invoiceRepository.findAllInvoicesByUser(securityService.getUser());
        }
    }

    @Override
    public void checkIfSupplierExistsForUser(String supplierName){
        User user = securityService.getUser();

        if(supplierRepository.findSupplierByUserAndName(user, supplierName.toUpperCase()).isPresent()){
            throw new IllegalArgumentException("Supplier name already exists. Please insert a different name for the Supplier.");
        }
    }

    @Override
    public void generatePdf(HttpServletResponse response, List<Invoice> invoiceList) throws IOException {
        Document document = new Document(PageSize.A4);

        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        fontTitle.setSize(20);

        Paragraph paragraph = new Paragraph("Invoice List", fontTitle);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100f);
        table.setWidths(new int[] { 3, 3, 3, 3, 3, 3, 3});
        table.setSpacingBefore(5);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(CMYKColor.DARK_GRAY);
        cell.setPadding(5);
        cell.setHorizontalAlignment(Cell.ALIGN_CENTER);

        Font font = FontFactory.getFont(FontFactory.TIMES_ROMAN);
        font.setColor(CMYKColor.WHITE);

        cell.setPhrase(new Phrase("Invoice Number", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Supplier Name", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Value", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Currency", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Due Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Introduction Date", font));
        table.addCell(cell);
        cell.setPhrase(new Phrase("Payment Status", font));
        table.addCell(cell);

        for (Invoice invoice: invoiceList) {
            table.addCell(String.valueOf(invoice.getInvoiceNumber()));
            table.addCell(String.valueOf(invoice.getSupplier().getSupplierName()));
            table.addCell(String.valueOf(invoice.getValue()));
            table.addCell(String.valueOf(invoice.getCurrency()));
            table.addCell(String.valueOf(invoice.getDueDate()));
            table.addCell(String.valueOf(invoice.getIntroductionDate()));
            table.addCell(String.valueOf(invoice.getStatus()));
        }

        table.setHorizontalAlignment(Table.ALIGN_CENTER);

        document.add(table);
        document.close();
    }
}
