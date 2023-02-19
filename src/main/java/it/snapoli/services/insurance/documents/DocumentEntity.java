package it.snapoli.services.insurance.documents;

import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.insurance.InsuranceEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue
    private int id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    @ToString.Exclude
    private CustomerEntity customer;

    @ManyToOne
    @JoinColumn(name = "insurance_id", referencedColumnName = "id")
    @ToString.Exclude
    private InsuranceEntity insurance;

    @NotNull
    private String type;

    private LocalDate expiry;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "document_id")
    private List<DocumentPartEntity> documentParts;
}
