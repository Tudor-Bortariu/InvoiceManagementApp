package ro.siit.FinalProject.mapstruct.dto.supplier;

import lombok.Data;

@Data
public abstract class BaseSupplierDto {
    private String supplierName;
    private String phoneNumber;
}
