package ro.siit.FinalProject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.mapstruct.dto.supplier.CreateSupplierDto;
import ro.siit.FinalProject.mapstruct.dto.supplier.UpdateSupplierDto;
import ro.siit.FinalProject.mapstruct.mapper.supplier.SupplierMapper;
import ro.siit.FinalProject.mapstruct.response.supplier.SupplierResponse;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.SupplierRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper mapper;
    private final SecurityService securityService;

    @Transactional
    public SupplierResponse createSupplier(CreateSupplierDto createSupplierDto){
        checkIfSupplierExistsForUser(createSupplierDto.getSupplierName());
        Supplier supplier = mapper.createSupplierDtoToSupplier(createSupplierDto);
        supplier.setUser(securityService.getAuthenticatedUser());
        supplierRepository.saveAndFlush(supplier);
        return mapper.supplierToSupplierResponse(supplier);
    }

    public List<SupplierResponse> findByUser_OrderBySupplierNameAsc(){
        return supplierRepository
                .findByUser_OrderBySupplierNameAsc(securityService.getAuthenticatedUser())
                .stream()
                .map(mapper::supplierToSupplierResponse)
                .collect(Collectors.toList());
    }

    public List<SupplierResponse> findByUser_AndSupplierName_OrderBySupplierNameAsc(String supplierName){
        return supplierRepository
                .findByUser_AndSupplierName_OrderBySupplierNameAsc(securityService.getAuthenticatedUser(), supplierName)
                .stream()
                .map(mapper::supplierToSupplierResponse)
                .collect(Collectors.toList());
    }

    public Supplier findByUser_AndSupplierName(String supplierName){
        return supplierRepository
                .findByUser_AndSupplierName(securityService.getAuthenticatedUser(), supplierName)
                .orElseThrow(ObjectNotFoundException::new);
    }

    public SupplierResponse findById(UUID supplierId){
        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(ObjectNotFoundException::new);
        return mapper.supplierToSupplierResponse(supplier);
    }

    public SupplierResponse updateSupplier(UpdateSupplierDto updateSupplierDto){
        Supplier supplier = supplierRepository.findById(updateSupplierDto.getId()).orElseThrow(ObjectNotFoundException::new);
        Supplier updatedSupplier = supplierRepository.saveAndFlush(mapper.updateSupplierDtoToSupplier(supplier, updateSupplierDto));
        return mapper.supplierToSupplierResponse(updatedSupplier);
    }

    @Transactional
    public void deleteById(UUID supplierId){
        supplierRepository.deleteById(supplierId);
    }

    private void checkIfSupplierExistsForUser(String supplierName){
        User user = securityService.getAuthenticatedUser();
        if(supplierRepository.findByUser_AndSupplierName(user, supplierName).isPresent()){
            throw new IllegalArgumentException("Supplier name already exists. Please insert a different name for the Supplier.");
        }
    }
}
