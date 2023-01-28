package it.snapoli.services.insurance.payments;

import it.snapoli.services.insurance.insurance.InsuranceEntity;
import lombok.Data;

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
    @JoinColumn(name = "insurancePaymentId")
    private InsuranceEntity insurancePayment;

    private LocalDateTime dateTime;

    private int insuranceId;

    private BigDecimal amount;

    private String type;


}
