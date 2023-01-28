package it.snapoli.services.insurance.insurance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InsuranceRepository extends JpaRepository<InsuranceEntity, Integer> {

    Page<InsuranceEntity> findAllByCustomerIdAndStatusNotInOrderByEndTimeAsc(int customerId, List<InsuranceEntity.Status> status, Pageable pageable);


    Page<InsuranceEntity> findAllByStatus(InsuranceEntity.Status status, Pageable pageable);

    @Query(countQuery = "select count(*) from InsuranceEntity i join i.insuredGood ig join i.customer c where i.status = :status and " +
            "            (ig.reference like :search or c.cf like :search or c.firstName like :search or c.lastName like :search or c.companyName like :search or c.lastName like :search )",
            value = "select i from InsuranceEntity i join i.insuredGood ig join i.customer c where  i.status = :status and" +
            " (ig.reference like :search or c.cf like :search or c.firstName like :search or c.lastName like :search or c.companyName like :search or c.lastName like :search )")
    Page<InsuranceEntity> search(@Param("status") InsuranceEntity.Status status,@Param("search") String q, Pageable pageable);
}
