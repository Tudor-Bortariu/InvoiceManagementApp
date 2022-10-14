package ro.siit.FinalProject.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @Column(name = "invoice_number", nullable = false)
    private String invoiceNumber;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private String dueDate;

    @Column(name = "introduction_date", nullable = false)
    private LocalDate introductionDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", nullable = false)
    private String status;

    public Invoice(String invoiceNumber, String supplierName, Double value, String currency, String dueDate, String status) {
        this.invoiceNumber = invoiceNumber;
        this.supplierName = supplierName;
        this.value = value;
        this.currency = currency;
        this.dueDate = dueDate;
        this.introductionDate = LocalDate.now();
        this.status = status;
    }

    public Invoice (){

    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public void setUser(User user) {
        this.user = user;
    }
}
