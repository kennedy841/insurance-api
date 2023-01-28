package it.snapoli.services.insurance.insurance;

import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.payments.InsurancePayment;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "insurance")
public class InsuranceEntity {

    @Id
    @GeneratedValue
    private int id;

    @OneToOne
    @JoinColumn(name = "customerId", referencedColumnName = "id")
    private CustomerEntity customer;


    @Enumerated(EnumType.STRING)
    private Status status;


    enum Status {
        ACTIVE, NOT_RENEWED,SUSPENDED, PENDING,EXPIRY,OUTOFCOVERAGE,EXPIRED;

        public static Status of(String s){
            if(s == null)
                return null;
            return Status.valueOf(s.toUpperCase());
        }
    }


    private String productName;

    private LocalDate startTime;

    private LocalDate buyTime;

    private LocalDate endTime;

    private LocalDate endCoverage;

    @OneToOne
    @JoinColumn(name = "companyId", referencedColumnName = "id")
    private InsuranceCompany company;

    @OneToMany
    @JoinColumn(name = "insurancePaymentId", referencedColumnName = "id")
    private List<InsurancePayment> payments;

    private BigDecimal total;

    private int brokerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "good_id", referencedColumnName = "id")
    private InsuredGood insuredGood;

    @Enumerated(EnumType.STRING)
    private InsuranceType branches;

    enum InsuranceType{
        RC, ELEMENTARY
    }



}
