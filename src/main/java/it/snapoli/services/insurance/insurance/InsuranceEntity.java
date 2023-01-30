package it.snapoli.services.insurance.insurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.payments.InsurancePayment;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

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

    public enum Status {
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

    private BigDecimal total;

    private BigDecimal payed = BigDecimal.ZERO;

    public InsuranceEntity pay(BigDecimal amount){
        this.setPayed(ofNullable(this.payed).map(val-> val.add(amount)).orElse(amount));
        return this;
    }

    public InsuranceEntity removePayment(BigDecimal amount){
        this.payed = payed.subtract(amount);
        return this;
    }
    public boolean isPayable(BigDecimal bigDecimal){
        if(payed == null)
            return true;
        return this.getToPay().compareTo(bigDecimal) >= 0;
    }

    public boolean shouldBePayed(){
        return total.subtract(payed).compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getToPay(){
        return total.subtract(payed);
    }


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
