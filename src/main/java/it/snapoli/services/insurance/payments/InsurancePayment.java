package it.snapoli.services.insurance.payments;

import it.snapoli.services.insurance.insurance.InsuranceEntity;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "insurance_payments")
public class InsurancePayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "insuranceId",referencedColumnName = "id")
    @ToString.Exclude
    private InsuranceEntity insurance;

    private LocalDateTime dateTime;

    private BigDecimal amount;

    private String type;


}
