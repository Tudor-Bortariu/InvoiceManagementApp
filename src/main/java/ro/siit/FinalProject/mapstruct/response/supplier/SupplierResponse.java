package ro.siit.FinalProject.mapstruct.response.supplier;

import lombok.Data;

import java.util.UUID;

@Data
public class SupplierResponse {
    private UUID id;
    private String supplierName;
    private String phoneNumber;
}
