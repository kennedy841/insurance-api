package it.snapoli.services.insurance.insurance;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.data.domain.PageRequest.of;


@RestController
@RequestMapping(path = "insurance_companies")
@RequiredArgsConstructor
public class InsuranceCompanyResource {

    private final InsuranceCompanyRepository insuranceRepository;

    @GetMapping
    public Page<InsuranceCompany> get(@RequestParam(name = "page", defaultValue = "1") int page, @RequestParam(name = "size", defaultValue = "10") int size) {
        return insuranceRepository.findAll(of(page-1, size));

    }
}
