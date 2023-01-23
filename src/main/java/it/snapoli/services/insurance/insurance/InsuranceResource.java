package it.snapoli.services.insurance.insurance;

import it.snapoli.services.insurance.customers.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

import static it.snapoli.services.insurance.insurance.InsuranceEntity.Status.NOT_RENEWED;
import static java.util.Optional.ofNullable;


@RestController
@RequestMapping(path = "insurances")
@RequiredArgsConstructor
public class InsuranceResource {

    private final InsuranceRepository insuranceRepository;
    private final InsuredGoodRepository insuredGoodRepository;
    private final CustomerRepository customerRepository;


    @GetMapping
    public Page<InsuranceEntity> getByCustomer(@RequestParam(name = "customerId") Integer customerId,
                                                 @RequestParam(name = "status") String status,
                                                 @RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "size", defaultValue = "10") int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        if (customerId == null) {
            return insuranceRepository.findAllByStatus(InsuranceEntity.Status.of(status), pageRequest);
        }

        return insuranceRepository.findAllByCustomerIdAndStatusNotInOrderByEndTimeAsc(customerId, List.of(NOT_RENEWED), pageRequest);

    }

    @GetMapping("/{id}")
    public InsuranceEntity get(@PathVariable int id){
        return insuranceRepository.findById(id).orElseThrow();
    }


    @PostMapping
    @Transactional
    public InsuranceEntity save(@RequestBody @Valid InsuranceEntity value) {
        InsuredGood save = insuredGoodRepository.save(value.getInsuredGood());
        value.setInsuredGood(save);
        value.setCustomer(customerRepository.findById(value.getCustomer().getId()).orElseThrow());
        value.setEndCoverage(ofNullable(value.getEndTime()).map(v -> v.plusDays(15)).orElse(null));
        return insuranceRepository.save(value);
    }


}
