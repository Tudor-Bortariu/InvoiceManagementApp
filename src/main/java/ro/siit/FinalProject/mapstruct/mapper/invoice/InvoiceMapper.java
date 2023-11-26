package ro.siit.FinalProject.mapstruct.mapper.invoice;

import org.mapstruct.*;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.mapstruct.dto.invoice.CreateInvoiceDto;
import ro.siit.FinalProject.mapstruct.dto.invoice.UpdateInvoiceDto;
import ro.siit.FinalProject.mapstruct.response.invoice.InvoiceResponse;
import ro.siit.FinalProject.model.Invoice;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring")
@Service
public interface InvoiceMapper {
    InvoiceResponse invoiceToInvoiceResponse(Invoice invoice);
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "introductionDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "invoiceImage", ignore = true)
    Invoice createInvoiceDtoToInvoice(CreateInvoiceDto createInvoiceDto);

    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "introductionDate", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "invoiceImage", ignore = true)
    Invoice updateInvoiceDtoToInvoice(@MappingTarget Invoice invoice, UpdateInvoiceDto updateInvoiceDto);
}
