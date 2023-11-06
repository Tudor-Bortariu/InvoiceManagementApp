package ro.siit.FinalProject.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDate;

@Entity
@Table(name = "invoices", uniqueConstraints = {@UniqueConstraint(columnNames = {"invoice_number", "user_id", "supplier_id"})})
@NoArgsConstructor
@Setter
@Getter
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoices_seq")
    @SequenceGenerator(name = "invoices_seq", sequenceName = "invoices_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "introduction_date", nullable = false)
    private Timestamp introductionDate;

    @ManyToOne
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name ="invoice_image")
    private byte[] invoiceImage;
}
