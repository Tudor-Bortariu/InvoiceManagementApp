package ro.siit.FinalProject.service;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.CMYKColor;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.InvoiceRepository;
import ro.siit.FinalProject.repository.SupplierRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final SecurityService securityService;
    private final SupplierRepository supplierRepository;

    public List<String> getRemainedInvoiceStatusOptions(Invoice invoice) {
        return Stream.of("Paid", "Not paid")
                .filter(status -> !status.equals(invoice.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Invoice> findByUser_OrderByIntroductionDateDesc(User user){
        return invoiceRepository.findByUser_OrderByIntroductionDateDesc(user);
    }

    @Transactional
    public Invoice saveInvoice(Invoice invoice){
        return invoiceRepository.saveAndFlush(invoice);
    }

    public Invoice findByInvoiceNumber_AndUser(String invoiceNumber, User user){
        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumber_AndUser(invoiceNumber, user);
        return invoice.orElse(null);
    }

    @Transactional
    public void deleteByInvoiceNumber(String invoiceNumber){
        invoiceRepository.deleteByInvoiceNumber(invoiceNumber);
    }

    public List<String> getRemainedInvoiceCurrencyOptions(Invoice invoice) {
        return Stream.of("RON", "EUR", "USD")
                .filter(status -> !status.equals(invoice.getCurrency()))
                .collect(Collectors.toList());
    }

    public void getInvoiceListWithCustomInputFilter(String filterParam, ModelAndView modelAndView) {
        if (filterParam.equals("Paid") || filterParam.equals("Not paid")) {
            modelAndView.addObject("filteredInvoiceList",
                    invoiceRepository.findByUser_AndStatus_OrderByDueDateAsc(securityService.getAuthenticatedUser(), filterParam));
        } else if (filterParam.equals("7") || filterParam.equals("30")) {
            modelAndView.addObject("filteredInvoiceList",
                    invoiceRepository.findByUser_AndDueDateBefore_AndStatus_OrderByDueDateAsc(securityService.getAuthenticatedUser(),
                            LocalDate.now().plusDays(Integer.parseInt(filterParam)), "Not paid"));
        } else {
            modelAndView.addObject("invoices", invoiceRepository.findByUser_OrderByIntroductionDateDesc(securityService.getAuthenticatedUser()));
        }
    }

    public List<Invoice> getPdfInvoiceList_WithCustomInputFilter(String filter) {
        if (filter.equals("Paid") || filter.equals("Not paid")) {
            return invoiceRepository.findByUser_AndStatus_OrderByDueDateAsc(securityService.getAuthenticatedUser(), filter);
        } else if (filter.equals("7") || filter.equals("30")) {
            return invoiceRepository.findByUser_AndDueDateBefore_AndStatus_OrderByDueDateAsc(securityService.getAuthenticatedUser(),
                    LocalDate.now().plusDays(Integer.parseInt(filter)), "Not paid");
        } else {
            return invoiceRepository.findByUser_OrderByIntroductionDateDesc(securityService.getAuthenticatedUser());
        }
    }

    public void checkIfSupplierExistsForUser(String supplierName){
        User user = securityService.getAuthenticatedUser();

        if(supplierRepository.findByUser_AndSupplierName(user, supplierName.toUpperCase()).isPresent()){
            throw new IllegalArgumentException("Supplier name already exists. Please insert a different name for the Supplier.");
        }
    }

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
