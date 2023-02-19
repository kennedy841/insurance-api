package it.snapoli.services.insurance.report;

import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.customers.CustomerRepository;
import it.snapoli.services.insurance.insurance.InsuranceEntity;
import it.snapoli.services.insurance.insurance.InsuranceRepository;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

@Path("/reports")
@RequiredArgsConstructor
public class ReportResource {


    private final GenerateDocReportInsuranceToPay generateWord;
    private final CustomerRepository customerRepository;

    private final InsuranceRepository insuranceRepository;

    @GET
    @Path("/insurances/to-pay/{customerId}")
    @Produces("application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    public Response generate1(@PathParam("customerId") int customerId) {
        CustomerEntity customer = customerRepository.getOne(customerId);
        ByteArrayInputStream bis = generateWord.generate(customer, insuranceRepository.findAllByCustomerId(customerId).stream().filter(InsuranceEntity::shouldBePayed).collect(Collectors.toList()));
        return Response.ok(bis).header("content-disposition", "attachment; filename = report.docx").build();
    }
}
