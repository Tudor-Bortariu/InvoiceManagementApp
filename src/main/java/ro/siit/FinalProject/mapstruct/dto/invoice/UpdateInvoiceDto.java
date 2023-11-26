package ro.siit.FinalProject.mapstruct.dto.invoice;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateInvoiceDto extends CreateInvoiceDto{
    private Integer id;
}
