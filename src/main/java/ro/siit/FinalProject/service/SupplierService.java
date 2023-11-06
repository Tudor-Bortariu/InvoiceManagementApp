package ro.siit.FinalProject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.siit.FinalProject.exception.ObjectNotFoundException;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;
import ro.siit.FinalProject.repository.SupplierRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SecurityService securityService;

    @Transactional
    public Supplier createSupplier(String supplierName, String phoneNumber){
        checkIfSupplierExistsForUser(supplierName);
        return supplierRepository.saveAndFlush(generateSupplier(supplierName, phoneNumber));
    }

    public Supplier saveSupplier(Supplier supplier){
        return supplierRepository.saveAndFlush(supplier);
    }

    public List<Supplier> findByUser_OrderBySupplierNameAsc(){
        User authenticatedUser = securityService.getAuthenticatedUser();
        return supplierRepository.findByUser_OrderBySupplierNameAsc(authenticatedUser);
    }

    public Supplier findById(UUID supplierId){
        return supplierRepository.findById(supplierId).orElseThrow(ObjectNotFoundException::new);
    }

    @Transactional
    public void deleteById(UUID supplierId){
        supplierRepository.deleteById(supplierId);
    }

    private Supplier generateSupplier(String supplierName, String phoneNumber) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(supplierName);
        supplier.setPhoneNumber(phoneNumber);
        supplier.setUser(securityService.getAuthenticatedUser());
        return supplier;
    }

    private void checkIfSupplierExistsForUser(String supplierName){
        User user = securityService.getAuthenticatedUser();
        if(supplierRepository.findByUser_AndSupplierName(user, supplierName).isPresent()){
            throw new IllegalArgumentException("Supplier name already exists. Please insert a different name for the Supplier.");
        }
    }
}
