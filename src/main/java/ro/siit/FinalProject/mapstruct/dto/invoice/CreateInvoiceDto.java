package ro.siit.FinalProject.mapstruct.dto.invoice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateInvoiceDto extends BaseInvoiceDto{
    private String supplier;
    private Double value;
    private String currency;
    private LocalDate dueDate;
    private String status;
    private MultipartFile invoiceImage;
}
