package ro.siit.FinalProject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
@NoArgsConstructor @Setter @Getter
public class Invoice {
    @Id
    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "introduction_date", nullable = false)
    private LocalDate introductionDate;

    @ManyToOne
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status;

    public Invoice(String invoiceNumber, Double value, String currency, LocalDate dueDate, String status, Supplier supplier) {
        this.invoiceNumber = invoiceNumber;
        this.value = value;
        this.currency = currency;
        this.dueDate = dueDate;
        this.introductionDate = LocalDate.now();
        this.status = status;
        this.supplier = supplier;
    }
}
