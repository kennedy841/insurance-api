package it.snapoli.services.insurance.broker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/brokers")
@RestController
public class BrokerResource {

    private final BrokerRepository brokerRepository;

    @GetMapping
    public Page<BrokerEntity> findAll(@RequestParam(name ="page",defaultValue = "1")  int page,
                                      @RequestParam(name = "size", defaultValue = "10") int size) {
        return brokerRepository.findAll(PageRequest.of(page-1, size));
    }
}
