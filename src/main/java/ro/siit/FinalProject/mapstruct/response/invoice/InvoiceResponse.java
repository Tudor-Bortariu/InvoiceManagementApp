package ro.siit.FinalProject.mapstruct.response.invoice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ro.siit.FinalProject.mapstruct.dto.invoice.BaseInvoiceDto;
import ro.siit.FinalProject.mapstruct.response.supplier.SupplierResponse;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceResponse extends BaseInvoiceDto {
    private Integer id;
    private Double value;
    private String currency;
    private LocalDate dueDate;
    private Timestamp introductionDate;
    private SupplierResponse supplier;
    private String status;
    private byte[] invoiceImage;
}
