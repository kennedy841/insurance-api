package it.snapoli.services.insurance.payments;

import it.snapoli.services.insurance.insurance.InsuranceEntity;
import it.snapoli.services.insurance.insurance.InsuranceEntity.Status;
import it.snapoli.services.insurance.insurance.InsuranceRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping(path = "customer-payments")
@RequiredArgsConstructor
@JBossLog
public class CustomerPaymentsResource {

    private final InsurancePaymentRepository insurancePaymentRepository;
    private final InsuranceRepository insuranceRepository;



    @PostMapping
    @Transactional
    public void save(@Valid @RequestBody CustomerPayment customerPayment) {
        List<InsuranceEntity> insuranceEntityPage = insuranceRepository.
                findAllByCustomerIdAndStatusNotInOrderByStartTimeAsc
                        (customerPayment.getCustomerId(), List.of(Status.EXPIRED, Status.NOT_RENEWED));


        BigDecimal amount = customerPayment.getAmount();
        List<InsuranceEntity> toPayments = insuranceEntityPage.stream().filter(InsuranceEntity::shouldBePayed).toList();

        for (InsuranceEntity insurance : toPayments) {
            BigDecimal currentPayedAmount = amount;
            if (insurance.isPayable(amount) && amount.compareTo(BigDecimal.ZERO) > 0) {
                if (amount.compareTo(insurance.getToPay()) >= 0) {
                    amount = amount.subtract(insurance.getToPay());
                    currentPayedAmount = insurance.getToPay();
                }
                else {
                    amount = BigDecimal.ZERO;
                }
                log.infof("pay insurance {} with {}",insurance, currentPayedAmount);
                InsurancePayment insurancePayment = new InsurancePayment();
                insurancePayment.setInsurance(insurance);
                insurancePayment.setType(customerPayment.getType());
                insurancePayment.setAmount(currentPayedAmount);
                insurancePayment.setDateTime(customerPayment.dateTime);

                insuranceRepository.save(insurance.pay(currentPayedAmount));
                insurancePaymentRepository.save(insurancePayment);
            }
        }

    }


    @Data
    public static class CustomerPayment {

        private int customerId;

        private BigDecimal amount;

        private LocalDateTime dateTime;

        private String type;

    }


}
