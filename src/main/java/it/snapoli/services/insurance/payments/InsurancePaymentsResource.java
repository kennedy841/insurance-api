package it.snapoli.services.insurance.payments;

import it.snapoli.services.insurance.insurance.InsuranceEntity;
import it.snapoli.services.insurance.insurance.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;


@RestController
@RequestMapping(path = "insurance-payments")
@RequiredArgsConstructor
public class InsurancePaymentsResource {

    private final InsurancePaymentRepository insurancePaymentRepository;
    private final InsuranceRepository insuranceRepository;

    @GetMapping
    public Page<InsurancePayment> get(@RequestParam(name = "insuranceId") Integer insuranceId) {
        return insurancePaymentRepository.findAllByInsuranceId(insuranceId);
    }

    @PostMapping
    @Transactional
    public InsurancePayment save(@Valid @RequestBody InsurancePayment insurancePayment) {
        InsuranceEntity insurance = insuranceRepository.getOne(insurancePayment.getInsuranceId());

        if (insurance.isPayable(insurancePayment.getAmount())) {
            insuranceRepository.save(insurance.pay(insurancePayment.getAmount()));
            return insurancePaymentRepository.save(insurancePayment);
        }
        throw new RuntimeException("not payable");
    }

    @DeleteMapping(path = "/{id}")
    public void save(@PathVariable int id) {
        InsurancePayment insurancePayment = insurancePaymentRepository.getOne(id);
        InsuranceEntity insurance = insuranceRepository.getOne(insurancePayment.getInsuranceId());
        insuranceRepository.save(insurance.removePayment(insurancePayment.getAmount()));
        insurancePaymentRepository.deleteById(id);
    }


}
