package ro.siit.FinalProject.mapstruct.dto.supplier;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateSupplierDto extends BaseSupplierDto{
    private UUID id;
}
