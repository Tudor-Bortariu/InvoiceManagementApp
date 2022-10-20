package ro.siit.FinalProject.model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @Column(name = "supplier_id")
    private UUID id;

    @Column(nullable = false, name = "supplier_name")
    private String supplierName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Invoice> invoices;

    public Supplier(UUID id, String supplierName, String phoneNumber) {
        this.id = id;
        this.supplierName = supplierName;
        this.phoneNumber = phoneNumber;
    }

    public Supplier(){

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
