package it.snapoli.services.insurance.customers;

import io.smallrye.common.constraint.NotNull;
import it.okkam.validation.FiscalCodeConf;
import it.okkam.validation.FiscalCodeValidator;
import it.snapoli.services.insurance.config.FiscalCodeFactory.Towns;
import lombok.*;
import lombok.experimental.Delegate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static it.snapoli.services.insurance.customers.CustomerEntity.CustomerType.CUSTOMER;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@RequiredArgsConstructor
@Path("/private-customers")
public class PrivateCustomerResource {

    private final CustomerRepository customerRepository;
    private final FiscalCodeConf fiscalCodeConf;


    private final Towns towns;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Page<CustomerEntity> getByPage(@QueryParam("page") int page, @QueryParam("size") int size, @QueryParam("q") String q) {

        if (q != null) {
            return customerRepository.search("%" + q + "%", List.of(CustomerEntity.CustomerType.CUSTOMER), PageRequest.of(page - 1, size));
        }

        return customerRepository.findAllByCustomerType(CUSTOMER, PageRequest.of(page - 1, size));
    }

    @POST
    @Path("/fiscal-codes")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public FiscalCode getFiscalCode(@Valid @NotNull FiscalCodeRequest fiscalCodeRequest) {
        return new FiscalCode(FiscalCodeValidator.calcoloCodiceFiscale(fiscalCodeConf, fiscalCodeRequest.getLastName(),
                fiscalCodeRequest.getFirstName(), fiscalCodeRequest.birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fiscalCodeRequest.townOfBirth, fiscalCodeRequest.gender));
    }

    @GET
    @Path("/cities")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Page<Towns.Entry> search(@QueryParam("q") String q) {

        if(isEmpty(q)){
            return new PageImpl<>(towns.getValues().stream().limit(100).collect(Collectors.toList()));
        }

        return new PageImpl<>(towns.getValues().stream().filter(v -> v.getValue().toLowerCase().contains(q.toLowerCase())).collect(Collectors.toList()));
    }

    @Value
    public static class FiscalCode {
        String[] values;

    }

    @Data
    public static class FiscalCodeRequest {
        private String firstName;

        private String lastName;
        private LocalDate birthDate;
        private String townOfBirth;
        private String gender;
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
        companies.setCustomerType(CUSTOMER);
        return customerRepository.save(companies);
    }
}
