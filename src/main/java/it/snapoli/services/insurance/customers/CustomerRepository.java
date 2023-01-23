package it.snapoli.services.insurance.customers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Integer> {

    @Query(value = "FROM CustomerEntity cs WHERE cs.firstName like :search or" +
            " cs.lastName like :search or cs.cf like :search or cs.piva like " +
            ":search or cs.companyName like :search and cs.customerType IN (:types)")
    Page<CustomerEntity> search(@Param("search") String search, List<CustomerEntity.CustomerType> types, Pageable pageable);

    Page<CustomerEntity> findAllByCustomerType(CustomerEntity.CustomerType customerType, Pageable pageable);

}
