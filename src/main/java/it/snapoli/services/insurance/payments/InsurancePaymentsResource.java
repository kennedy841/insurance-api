package it.snapoli.services.insurance.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(path = "insurance-payments")
@RequiredArgsConstructor
public class InsurancePaymentsResource {

    private final InsurancePaymentRepository insurancePaymentRepository;


    @GetMapping
    public Page<InsurancePayment> get(@RequestParam(name = "insuranceId") Integer insuranceId) {
        return insurancePaymentRepository.findAllByInsuranceId(insuranceId);

    }





}
