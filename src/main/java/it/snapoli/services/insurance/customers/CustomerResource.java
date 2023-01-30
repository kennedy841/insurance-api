package it.snapoli.services.insurance.customers;

import it.snapoli.services.insurance.customers.CustomerEntity.CustomerType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Path("/customers")
public class CustomerResource {

    private final CustomerRepository customerRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<CustomerEntity> getByPage(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("q") String q, @QueryParam("customerType") CustomerType customerType) {

        if(!StringUtils.isEmpty(q)){
            return customerRepository.search("%"+q+"%", getCustomerTypes(customerType),PageRequest.of(page - 1, size));
        }

        if(customerType == null){
            return customerRepository.findAll(PageRequest.of(page - 1, size));
        }

        return customerRepository.findAllByCustomerType(customerType,PageRequest.of(page - 1, size));
    }

    private static List<CustomerType> getCustomerTypes(CustomerType customerType) {
        List<CustomerType> all = new ArrayList<>();
        if(customerType != null){
            all.add(customerType);
        }
        else {
            all.add(CustomerType.CUSTOMER);
            all.add(CustomerType.COMPANY);
        }
        return all;
    }

}
