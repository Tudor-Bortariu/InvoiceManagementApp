package ro.siit.FinalProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaInvoiceRepository extends JpaRepository<Invoice, UUID> {

    @Query(value = "SELECT i FROM Invoice i WHERE i.user = :user")
    List<Invoice> findAllInvoicesByUser(User user);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Invoice i WHERE i.invoiceNumber=:invoiceNumber")
    void deleteByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);

    @Query(value = "SELECT i FROM Invoice i WHERE i.invoiceNumber = :invoiceNumber")
    Optional<Invoice> findInvoiceByNumber(@Param("invoiceNumber") String invoiceNumber);

    @Query(value = "SELECT i FROM Invoice i WHERE i.user = :user AND i.status = :status")
    List<Invoice> findInvoicesByStatus(User user, String status);

    @Query(value = "SELECT i FROM Invoice i WHERE i.user = :user AND i.supplier.supplierName = :supplierName")
    List<Invoice> findInvoiceBySupplierAndUser(User user, @Param("supplierName") String supplierName);
}
