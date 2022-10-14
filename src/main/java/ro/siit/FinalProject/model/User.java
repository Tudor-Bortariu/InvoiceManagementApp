package ro.siit.FinalProject.model;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false, length = 64)
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Invoice> invoices;

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }
}
