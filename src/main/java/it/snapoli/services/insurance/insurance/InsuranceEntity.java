package it.snapoli.services.insurance.insurance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.documents.DocumentEntity;
import it.snapoli.services.insurance.payments.InsurancePayment;
import it.snapoli.services.insurance.xceptions.ForeignKeyException;
import lombok.Data;
import lombok.ToString;

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
    @ToString.Exclude
    private CustomerEntity customer;


    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "insurance")
    @JsonIgnore
    @ToString.Exclude
    private List<InsurancePayment> paymentHistories;

    @OneToMany(mappedBy = "insurance")
    @JsonIgnore
    @ToString.Exclude
    private List<DocumentEntity> documents;

    public enum Status {
        ACTIVE, NOT_RENEWED, SUSPENDED, PENDING, EXPIRY, OUTOFCOVERAGE, EXPIRED, TO_CASH;

        public static Status of(String s) {
            if (s == null)
                return null;
            return Status.valueOf(s.toUpperCase());
        }
    }


    @PreRemove
    public void preDelete(){
        if(!paymentHistories.isEmpty()){
            throw new ForeignKeyException("remove payments");
        }
        if(!documents.isEmpty()){
            throw new ForeignKeyException("remove documents");
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

    private BigDecimal price;

    private BigDecimal billingAmount = BigDecimal.ZERO;

    public boolean isWithBilling() {
        return Optional.ofNullable(billingAmount).orElse(BigDecimal.ZERO).intValue() != 0;
    }

    private BigDecimal payed = BigDecimal.ZERO;

    public InsuranceEntity pay(BigDecimal amount) {
        this.setPayed(ofNullable(this.payed).map(val -> val.add(amount)).orElse(amount));
        return this;
    }

    public InsuranceEntity removePayment(BigDecimal amount) {
        this.payed = payed.subtract(amount);
        return this;
    }

    public boolean isPayable(BigDecimal bigDecimal) {
        if (payed == null)
            return true;
        return this.getToPay().compareTo(bigDecimal) >= 0;
    }

    public BigDecimal getTotal(){
        return price.add(Optional.ofNullable(billingAmount).orElse(BigDecimal.ZERO));
    }

    public boolean shouldBePayed() {
        return price.subtract(payed).compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getToPay() {
        return getTotal().subtract(Optional.ofNullable(payed).orElse(BigDecimal.ZERO));
    }


    private int brokerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "good_id", referencedColumnName = "id")
    private InsuredGood insuredGood;

    @Enumerated(EnumType.STRING)
    private InsuranceType branches;

    enum InsuranceType {
        RC, ELEMENTARY
    }


}
