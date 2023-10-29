package ro.siit.FinalProject.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.siit.FinalProject.model.Supplier;
import ro.siit.FinalProject.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaSupplierRepository extends JpaRepository<Supplier, UUID> {
    @Query(value = "SELECT s FROM #{#entityName} s WHERE s.user = :user ORDER BY s.supplierName ASC")
    List<Supplier> findAllSuppliersByUser(User user);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM #{#entityName} s WHERE s.id=:id")
    void deleteBySupplierId(@Param("id") UUID id);

    @Query(value = "SELECT s FROM #{#entityName} s WHERE s.user = :user AND s.supplierName = :supplierName")
    Optional<Supplier> findSupplierByUserAndName(User user, @Param("supplierName") String supplierName);

    @Query(value = "SELECT s FROM #{#entityName} s WHERE s.user = :user AND s.supplierName != :supplierName ORDER BY s.supplierName ASC")
    List<Supplier> supplierListWithoutCurrentSupplier(User user, @Param("supplierName") String currentSupplierName);
}
