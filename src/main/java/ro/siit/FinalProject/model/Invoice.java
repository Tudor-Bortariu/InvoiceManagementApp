package ro.siit.FinalProject.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private String dueDate;

    @Column(name = "introduction_date", nullable = false)
    private LocalDate introductionDate;

    @ManyToOne
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status;

    public Invoice(String invoiceNumber, Double value, String currency, String dueDate, String status, Supplier supplier) {
        this.invoiceNumber = invoiceNumber;
        this.value = value;
        this.currency = currency;
        this.dueDate = dueDate;
        this.introductionDate = LocalDate.now();
        this.status = status;
        this.supplier = supplier;
    }

    public Invoice (){

    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getIntroductionDate() {
        return introductionDate;
    }

    public User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
