package ro.siit.FinalProject.model;


import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "suppliers")
@NoArgsConstructor
public class Supplier {

    @Id
    @Column(name = "supplier_id")
    private UUID id;

    @Column(nullable = false, name = "supplier_name", unique = true)
    private String supplierName;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String county;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Invoice> invoices;

    public Supplier(UUID id, String supplierName, String phoneNumber, String county) {
        this.id = id;
        this.supplierName = supplierName.toUpperCase();
        this.phoneNumber = phoneNumber;
        this.county = county.toUpperCase();
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
        this.supplierName = supplierName.toUpperCase();
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county.toUpperCase();
    }
}
