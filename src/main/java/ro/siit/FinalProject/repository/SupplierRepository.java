package ro.siit.FinalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    List<Supplier> findByUser_OrderBySupplierNameAsc(User user);

    Optional<Supplier> findByUser_AndSupplierName(User user, String supplierName);

    List<Supplier> findByUser_AndSupplierName_OrderBySupplierNameAsc(User user, String currentSupplierName);
}
