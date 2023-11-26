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
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.mapstruct.dto.invoice.CreateInvoiceDto;
import ro.siit.FinalProject.mapstruct.dto.invoice.UpdateInvoiceDto;
import ro.siit.FinalProject.mapstruct.mapper.invoice.InvoiceMapper;
import ro.siit.FinalProject.mapstruct.response.invoice.InvoiceResponse;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.InvoiceRepository;
import ro.siit.FinalProject.repository.SupplierRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InvoiceService {
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper mapper;
    private final SecurityService securityService;
    private final SupplierRepository supplierRepository;

    public List<String> getRemainedInvoiceStatusOptions(String invoiceStatus) {
        return Stream.of("Paid", "Not paid")
                .filter(status -> !status.equals(invoiceStatus))
                .collect(Collectors.toList());
    }

    public InvoiceResponse findById(Integer id){
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(ObjectNotFoundException::new);
        return mapper.invoiceToInvoiceResponse(invoice);
    }

    public List<InvoiceResponse> findByUser_OrderByIntroductionDateDesc(){
        return invoiceRepository
                .findByUser_OrderByIntroductionDateDesc(securityService.getAuthenticatedUser())
                .stream()
                .map(mapper::invoiceToInvoiceResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceDto createInvoiceDto) throws IOException {
        checkInvoiceNumberExists(createInvoiceDto.getInvoiceNumber());
        Invoice invoice = mapper.createInvoiceDtoToInvoice(createInvoiceDto);
        User user = securityService.getAuthenticatedUser();
        Supplier supplier = supplierRepository.findByUser_AndSupplierName(user, createInvoiceDto.getSupplier())
                .orElseThrow(ObjectNotFoundException::new);
        invoice.setUser(user);
        invoice.setSupplier(supplier);
        if(createInvoiceDto.getInvoiceImage() != null){
            System.out.println(createInvoiceDto.getInvoiceImage());
            invoice.setInvoiceImage(createInvoiceDto.getInvoiceImage().getBytes());
        }
        System.out.println(Arrays.toString(invoice.getInvoiceImage()));
        invoiceRepository.saveAndFlush(invoice);
        return mapper.invoiceToInvoiceResponse(invoice);
    }

    @Transactional
    public InvoiceResponse updateInvoice(Integer invoiceId, UpdateInvoiceDto updateInvoiceDto){
        Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
        Supplier updatedSupplier = supplierRepository.findByUser_AndSupplierName(securityService.getAuthenticatedUser(), updateInvoiceDto.getSupplier())
                .orElseThrow(ObjectNotFoundException::new);
        Invoice updatedInvoice = mapper.updateInvoiceDtoToInvoice(invoice, updateInvoiceDto);
        invoice.setSupplier(updatedSupplier);
        return mapper.invoiceToInvoiceResponse(invoiceRepository.saveAndFlush(updatedInvoice));
    }

    @Transactional
    public InvoiceResponse changePaymentStatus(String invoiceNumber){
        Invoice invoice = invoiceRepository.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser())
                .orElseThrow(ObjectNotFoundException::new);
        if(invoice.getStatus().equals("Not paid")) {
            invoice.setStatus("Paid");
        } else {
            invoice.setStatus("Not paid");
        }
        return mapper.invoiceToInvoiceResponse(invoiceRepository.saveAndFlush(invoice));
    }

    private void checkInvoiceNumberExists(String invoiceNumber) {
        Optional<Invoice> invoice = invoiceRepository.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser());
        if (invoice.isPresent()) {
            throw new IllegalArgumentException("Invoice number already exists in this database. Please insert a different number for the Invoice.");
        }
    }

    public InvoiceResponse findByInvoiceNumber_AndUser(String invoiceNumber){
        Invoice invoice = invoiceRepository
                .findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser())
                .orElseThrow(ObjectNotFoundException::new);
        return mapper.invoiceToInvoiceResponse(invoice);
    }

    @Transactional
    public void deleteById(Integer invoiceId){
        invoiceRepository.deleteById(invoiceId);
    }

    public List<String> getRemainedInvoiceCurrencyOptions(String currency) {
        return Stream.of("RON", "EUR", "USD")
                .filter(status -> !status.equals(currency))
                .collect(Collectors.toList());
    }

    public byte[] getInvoiceImage(String invoiceNumber){
        Invoice invoice = invoiceRepository.findByInvoiceNumber_AndUser(invoiceNumber, securityService.getAuthenticatedUser())
                .orElseThrow(ObjectNotFoundException::new);
        if(invoice.getInvoiceImage() != null) {
            return invoice.getInvoiceImage();
        }
        throw new NullPointerException("Selected Invoice does not have a picture assigned.");
    }

    @Transactional
    public void deleteResourceForInvoice(Integer invoiceId){
        Invoice invoice = invoiceRepository.getReferenceById(invoiceId);
        invoice.setInvoiceImage(null);
        invoiceRepository.saveAndFlush(invoice);
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
