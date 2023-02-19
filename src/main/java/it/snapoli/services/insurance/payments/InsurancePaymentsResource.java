package it.snapoli.services.insurance.payments;

import it.snapoli.services.insurance.insurance.InsuranceEntity;
import it.snapoli.services.insurance.insurance.InsuranceRepository;
import it.snapoli.services.insurance.xceptions.BusinessLogicError;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public InsurancePayment save(@Valid @RequestBody InsurancePaymentDto insurancePaymentDto) {
        InsuranceEntity insurance = insuranceRepository.getOne(insurancePaymentDto.getInsuranceId());

        if (insurance.isPayable(insurancePaymentDto.getAmount())) {
            insuranceRepository.save(insurance.pay(insurancePaymentDto.getAmount()));
            InsurancePayment insurancePayment = Mappers.getMapper(InsurancePaymentDto.DtoMapper.class).from(insurancePaymentDto);
            insurancePayment.setInsurance(insurance);
            return insurancePaymentRepository.save(insurancePayment);
        }
        throw new BusinessLogicError("not payable");
    }

    @DeleteMapping(path = "/{id}")
    @Transactional
    public void delete(@PathVariable int id) {
        InsurancePayment insurancePayment = insurancePaymentRepository.getOne(id);
        InsuranceEntity insurance = insuranceRepository.getOne(insurancePayment.getInsurance().getId());
        insuranceRepository.save(insurance.removePayment(insurancePayment.getAmount()));
        insurancePaymentRepository.deleteById(id);
    }

    @Data
    public static class InsurancePaymentDto{

        private int insuranceId;

        private LocalDateTime dateTime;

        private BigDecimal amount;

        private String type;

        @Mapper
        public interface DtoMapper {

            InsurancePayment from(InsurancePaymentDto insurancePaymentDto);
        }
    }


}
