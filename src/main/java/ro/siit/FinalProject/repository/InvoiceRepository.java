package ro.siit.FinalProject.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ro.siit.FinalProject.model.Invoice;
import ro.siit.FinalProject.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByUser_OrderByIntroductionDateDesc(User user);

    @Transactional
    @Modifying
    void deleteByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByInvoiceNumber_AndUser(String invoiceNumber, User user);

    List<Invoice> findByUser_AndStatus_OrderByDueDateAsc(User user, String status);

    @Query(value = "SELECT i FROM #{#entityName} i WHERE i.user = ?1 AND i.dueDate <= ?2 AND i.status = ?3 ORDER BY i.dueDate ASC")
    List<Invoice> findByUser_AndDueDateBefore_AndStatus_OrderByDueDateAsc(User user, LocalDate maximumDueDate, String status);
}
