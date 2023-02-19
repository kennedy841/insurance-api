package it.snapoli.services.insurance.insurance;

import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.customers.CustomerRepository;
import it.snapoli.services.insurance.insurance.InsuranceEntity.Status;
import it.snapoli.services.insurance.notes.NoteRepository;
import it.snapoli.services.insurance.notes.NotesEntity;
import it.snapoli.services.insurance.payments.InsurancePayment;
import it.snapoli.services.insurance.payments.InsurancePaymentRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static it.snapoli.services.insurance.insurance.InsuranceEntity.Status.*;
import static java.util.Optional.ofNullable;


@RestController
@RequestMapping(path = "insurances")
@RequiredArgsConstructor
public class InsuranceResource {

    private final InsuranceRepository insuranceRepository;
    private final InsuredGoodRepository insuredGoodRepository;
    private final CustomerRepository customerRepository;

    private final InsurancePaymentRepository insurancePaymentRepository;

    private final InsuranceMappers insuranceMappers;

    private final NoteRepository noteRepository;


    @GetMapping
    public Page<InsuranceEntity> getInsurances(@RequestParam(name = "customerId") Integer customerId,
                                               @RequestParam(name = "status") String status,
                                               @RequestParam(name = "groupStatus") String groupStatus,
                                               @RequestParam(name = "toEndCoverage") LocalDate toEndCoverage,
                                               @RequestParam(name = "page", defaultValue = "1") int page,
                                               @RequestParam(name = "size", defaultValue = "10") int size, @RequestParam(name = "q") String q) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if (customerId == null) {
            Status domainStatus = Status.of(status);

            if(groupStatus != null && domainStatus == null){
                return insuranceRepository.findAllByStatus(Status.of(groupStatus), pageRequest);
            }

            if (StringUtils.hasText(q)) {
                return insuranceRepository.search(domainStatus, "%" + q + "%", pageRequest);
            }

            if (domainStatus == EXPIRY) {
                return insuranceRepository.findAllByEndCoverageBeforeOrderByEndCoverageAsc(LocalDate.now().plusDays(30), pageRequest);
            }

            if(domainStatus == TO_CASH){
                return new PageImpl<>(insuranceRepository.findAll(pageRequest).stream().filter(InsuranceEntity::shouldBePayed).collect(Collectors.toList()));
            }

            if(domainStatus != null){
                return insuranceRepository.findAllByStatus(domainStatus, pageRequest);
            }
            if(toEndCoverage!=null){
                return insuranceRepository.findAllByEndCoverageIsBefore(toEndCoverage, pageRequest);
            }

            return insuranceRepository.findAll(pageRequest);

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
        CustomerEntity customer = customerRepository.findById(req.getCustomer().getId()).orElseThrow();
        req.setCustomer(customer);
        req.setEndCoverage(ofNullable(req.getEndTime()).map(v -> v.plusDays(15)).orElse(null));

        InsuranceEntity insuranceEntity = insuranceRepository.save(insuranceMappers.from(req));

        if (req.payments != null) {
            insuranceRepository.save(insuranceEntity.pay(req.payments.getAmount()));
            InsuranceCreateRequest.InsurancePaymentDto payments = req.payments;
            InsurancePayment insurancePayment = new InsurancePayment();
            insurancePayment.setInsurance(insuranceEntity);
            insurancePayment.setAmount(payments.getAmount());
            insurancePayment.setDateTime(LocalDateTime.now());
            insurancePayment.setType(payments.getType());
            insurancePaymentRepository.save(insurancePayment);
        }

        if(req.getInsuredGood().getNote() != null){
            noteRepository.save(NotesEntity.builder()
                .dateTime(LocalDateTime.now())
                .referenceEntity(String.valueOf(customer.getId()))
                .text(req.getInsuredGood().getNote())
                .referenceEntityType("insurance")
                .build());
        }

        return insuranceEntity;
    }


    @Getter
    @Setter
    public static class InsuranceCreateRequest extends InsuranceEntity {
        private InsurancePaymentDto payments;

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
