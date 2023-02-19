package it.snapoli.services.insurance.customers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.smallrye.common.constraint.NotNull;
import it.snapoli.services.insurance.documents.DocumentEntity;
import it.snapoli.services.insurance.xceptions.ForeignKeyException;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.bind.annotation.DeleteMapping;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @GeneratedValue
    private int id;

    private String cf;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    @ToString.Exclude
    private List<DocumentEntity> documents;

    public String getDisplayName(){
        if(customerType == CustomerType.COMPANY){
            return companyName;
        }
        return firstName + " "+lastName;
    }

    @DeleteMapping
    public void onDelete(){
        if(!documents.isEmpty()){
            throw new ForeignKeyException("remove documents");
        }
    }


    @NotNull
    private String fiscalAddress;

    @NotNull
    private String fiscalZipcode;

    @NotNull
    private String fiscalCity;

    @NotNull
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

    private String email;

    private int brokerId;

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String gender;


    private String address;

    private String city;

    private String zipcode;

    private String companyName;

    private String piva;

    private int companySize;

    public enum CustomerType{
        CUSTOMER,COMPANY
    }
}
