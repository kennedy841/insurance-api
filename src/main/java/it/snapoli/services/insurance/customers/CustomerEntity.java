package it.snapoli.services.insurance.customers;

import io.smallrye.common.constraint.NotNull;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "customer")
public class CustomerEntity {

    @Id
    @GeneratedValue
    private int id;

    private String cf;


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
