package ro.siit.FinalProject.mapstruct.mapper.supplier;

import org.mapstruct.*;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.mapstruct.dto.supplier.CreateSupplierDto;
import ro.siit.FinalProject.mapstruct.dto.supplier.UpdateSupplierDto;
import ro.siit.FinalProject.mapstruct.response.supplier.SupplierResponse;
import ro.siit.FinalProject.model.Supplier;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        componentModel = "spring")
@Service
public interface SupplierMapper {
    SupplierResponse supplierToSupplierResponse(Supplier supplier);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Supplier createSupplierDtoToSupplier(CreateSupplierDto createSupplierDto);
    @Mapping(target = "user", ignore = true)
    Supplier updateSupplierDtoToSupplier(@MappingTarget Supplier supplier, UpdateSupplierDto updateSupplierDto);
}
