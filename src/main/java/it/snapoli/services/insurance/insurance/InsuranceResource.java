package it.snapoli.services.insurance.insurance;

import it.snapoli.services.insurance.customers.CustomerRepository;
import it.snapoli.services.insurance.insurance.InsuranceEntity.Status;
import it.snapoli.services.insurance.payments.InsurancePayment;
import it.snapoli.services.insurance.payments.InsurancePaymentRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

import static it.snapoli.services.insurance.insurance.InsuranceEntity.Status.NOT_RENEWED;
import static java.util.Optional.ofNullable;


@RestController
@RequestMapping(path = "insurances")
@RequiredArgsConstructor
public class InsuranceResource {

    private final InsuranceRepository insuranceRepository;
    private final InsuredGoodRepository insuredGoodRepository;
    private final CustomerRepository customerRepository;

    private final InsurancePaymentRepository insurancePaymentRepository;

    private final InsuranceCreateRequest.Mappers insuranceMappers;


    @GetMapping
    public Page<InsuranceEntity> getInsurances(@RequestParam(name = "customerId") Integer customerId,
                                               @RequestParam(name = "status") String status,
                                               @RequestParam(name = "page", defaultValue = "1") int page,
                                               @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(name = "q") String q) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if (customerId == null) {
            if (StringUtils.hasText(q)) {
                return insuranceRepository.search(Status.of(status), "%" + q + "%", pageRequest);
            }
            return insuranceRepository.findAllByStatus(Status.of(status), pageRequest);
        }

        return insuranceRepository.findAllByCustomerIdAndStatusNotInOrderByEndTimeAsc(customerId, List.of(NOT_RENEWED), pageRequest);

    }

    @GetMapping("/{id}")
    public InsuranceEntity get(@PathVariable int id) {
        return insuranceRepository.findById(id).orElseThrow();
    }


    @PostMapping
    @Transactional
    public InsuranceEntity save(@RequestBody @Valid InsuranceCreateRequest req) {
        InsuredGood save = insuredGoodRepository.save(req.getInsuredGood());
        req.setInsuredGood(save);
        req.setCustomer(customerRepository.findById(req.getCustomer().getId()).orElseThrow());
        req.setEndCoverage(ofNullable(req.getEndTime()).map(v -> v.plusDays(15)).orElse(null));

        InsuranceEntity insuranceEntity = insuranceRepository.save(insuranceMappers.from(req));

        if (req.payments != null) {
            insuranceRepository.save(insuranceEntity.pay(req.payments.getAmount()));
            InsuranceCreateRequest.InsurancePaymentDto payments = req.payments;
            InsurancePayment insurancePayment = new InsurancePayment();
            insurancePayment.setInsuranceId(insuranceEntity.getId());
            insurancePayment.setAmount(payments.getAmount());
            insurancePayment.setDateTime(LocalDateTime.now());
            insurancePayment.setType(payments.getType());
            insurancePaymentRepository.save(insurancePayment);
        }

        return insuranceEntity;
    }


    @Getter
    @Setter
    public static class InsuranceCreateRequest extends InsuranceEntity {
        private InsurancePaymentDto payments;

        @Mapper(componentModel = "cdi")
        public interface Mappers {
            InsuranceEntity from(InsuranceCreateRequest insuranceCreateRequest);
        }

        @Data
        public static class InsurancePaymentDto {
            private BigDecimal amount;
            private String type;

            private String mode;

        }

    }

    @PutMapping("/{id}")
    @Transactional
    public InsuranceEntity update(@PathVariable int id, @RequestBody @Valid InsuranceEntity value) {
        insuredGoodRepository.save(value.getInsuredGood());
        value.setEndCoverage(ofNullable(value.getEndTime()).map(v -> v.plusDays(15)).orElse(null));
        return insuranceRepository.save(value);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public void delete(@PathVariable int id) {
        insurancePaymentRepository.deleteAll(insurancePaymentRepository.findAllByInsuranceId(id));

        insuranceRepository.deleteById(id);
    }


}
