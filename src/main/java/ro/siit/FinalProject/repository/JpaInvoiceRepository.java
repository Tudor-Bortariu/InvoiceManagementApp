package ro.siit.FinalProject.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface JpaInvoiceRepository extends JpaRepository<Invoice, String> {
    @Query(value = "SELECT i FROM #{#entityName} i WHERE i.user = :user ORDER BY i.introductionDate DESC")
    List<Invoice> findAllInvoicesByUser(User user);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM #{#entityName} i WHERE i.invoiceNumber=:invoiceNumber")
    void deleteByInvoiceNumber(@Param("invoiceNumber") String invoiceNumber);
    @Query(value = "SELECT i FROM #{#entityName} i WHERE i.user = :user AND i.invoiceNumber = :invoiceNumber")
    Optional<Invoice> findInvoiceByNumberAndUser(@Param("invoiceNumber") String invoiceNumber, User user);
    @Query(value = "SELECT i FROM #{#entityName} i WHERE i.user = :user AND i.status = :status ORDER BY i.dueDate ASC")
    List<Invoice> findInvoiceByStatus(User user, String status);
    @Query(value = "SELECT i FROM #{#entityName} i WHERE i.user = :user AND i.status = :status AND i.dueDate <= :maximumDueDate ORDER BY i.dueDate ASC")
    List<Invoice> findInvoiceByDueDate (User user, LocalDate maximumDueDate, String status);
}
