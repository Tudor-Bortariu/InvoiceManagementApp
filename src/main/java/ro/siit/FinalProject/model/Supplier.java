package ro.siit.FinalProject.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Table(name = "suppliers")
@Entity
@NoArgsConstructor @Setter @Getter
public class Supplier {

    @Id
    @Column(name = "supplier_id")
    private UUID id;

    @Column(nullable = false, name = "supplier_name")
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
}
