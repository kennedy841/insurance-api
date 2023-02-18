package it.snapoli.services.insurance.documents;

import it.snapoli.services.insurance.customers.CustomerEntity;
import it.snapoli.services.insurance.insurance.InsuranceEntity;
import lombok.Data;
import org.mapstruct.Mapper;

import java.time.LocalDate;

@Data
public class DocumentsDto {
    private int id;


    private CustomerEntity customer;

    private InsuranceEntity insurance;
    private String type;

    private LocalDate expiry;


    @Mapper
    public interface DocumentMapper {

        DocumentsDto from(DocumentEntity documentEntity);
    }
}
