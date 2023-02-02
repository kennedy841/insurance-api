package it.snapoli.services.insurance.insurance;

import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface InsuranceMappers {
    InsuranceEntity from(InsuranceResource.InsuranceCreateRequest insuranceCreateRequest);
}
