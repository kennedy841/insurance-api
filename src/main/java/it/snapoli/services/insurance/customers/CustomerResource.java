package it.snapoli.services.insurance.customers;

import it.snapoli.services.insurance.customers.CustomerEntity.CustomerType;
import it.snapoli.services.insurance.insurance.InsuranceEntity;
import it.snapoli.services.insurance.insurance.InsuranceRepository;
import it.snapoli.services.insurance.insurance.InsuranceRepository.CountByStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.snapoli.services.insurance.insurance.InsuranceEntity.Status.ACTIVE;
import static it.snapoli.services.insurance.insurance.InsuranceEntity.Status.EXPIRY;

@RequiredArgsConstructor
@Path("/customers")
public class CustomerResource {

    private final CustomerRepository customerRepository;
    private final InsuranceRepository insuranceRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<CustomerEntity> getByPage(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("q") String q, @QueryParam("customerType") CustomerType customerType) {

        if (!StringUtils.isEmpty(q)) {
            return customerRepository.search("%" + q + "%", getCustomerTypes(customerType), PageRequest.of(page - 1, size));
        }

        if (customerType == null) {
            return customerRepository.findAll(PageRequest.of(page - 1, size));
        }

        return customerRepository.findAllByCustomerType(customerType, PageRequest.of(page - 1, size));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/insurances/counters")
    public Page<CountResponse> getInfos() {


        List<CountResponse> list = new ArrayList<>();

        List<CountByStatus> collect = insuranceRepository.countByStatus().stream().filter(v -> Arrays.asList(ACTIVE, EXPIRY).contains(v.getStatus())).toList();

        Map<Integer, List<CountByStatus>> collect1 = collect.stream().collect(Collectors.groupingBy(CountByStatus::getCustomerId));

        collect1.forEach((integer, countByStatuses) -> list.add(new CountResponse(integer,integer,
                countByStatuses.stream().filter(v -> v.getStatus() == ACTIVE).findFirst().map(CountByStatus::getCount).orElse(0L),
                countByStatuses.stream().filter(v -> v.getStatus() == EXPIRY).findFirst().map(CountByStatus::getCount).orElse(0L)
        )));

        return new PageImpl<>(list);

    }

    @AllArgsConstructor
    @Getter
    public static class CountResponse {

        private Integer id;
        private Integer customerId;

        private Long activeInsurances;

        private Long expiringInsurances;
    }

    private static List<CustomerType> getCustomerTypes(CustomerType customerType) {
        List<CustomerType> all = new ArrayList<>();
        if (customerType != null) {
            all.add(customerType);
        } else {
            all.add(CustomerType.CUSTOMER);
            all.add(CustomerType.COMPANY);
        }
        return all;
    }

}
