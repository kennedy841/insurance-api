package it.snapoli.services.insurance.customers;

import io.smallrye.common.constraint.NotNull;
import it.snapoli.services.insurance.customers.CustomerEntity.CustomerType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

import static it.snapoli.services.insurance.customers.CustomerEntity.CustomerType.COMPANY;
import static it.snapoli.services.insurance.customers.CustomerEntity.CustomerType.CUSTOMER;

@RequiredArgsConstructor
@Path("/companies")
public class CompanyCustomerResource {

    private final CustomerRepository customerRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<CustomerEntity> getByPage(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("q") String q) {

        if(q != null){
            return customerRepository.search("%"+q+"%", List.of(CustomerType.COMPANY),PageRequest.of(page - 1, size));
        }

        return customerRepository.findAllByCustomerType(CustomerType.COMPANY,PageRequest.of(page - 1, size));
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Optional<CustomerEntity> getByPage(@PathParam("id") int id) {
        return customerRepository.findById(id);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CustomerEntity save(@Valid @NotNull CustomerEntity companies) {
        companies.setCustomerType(COMPANY);
        return customerRepository.save(companies);
    }
}
