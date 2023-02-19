package it.snapoli.services.insurance.insurance;

import it.snapoli.services.insurance.insurance.InsuranceEntity.Status;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InsuranceRepository extends JpaRepository<InsuranceEntity, Integer> {

    Page<InsuranceEntity> findAllByCustomerIdAndStatusNotInOrderByEndTimeAsc(int customerId, List<Status> status, Pageable pageable);

    List<InsuranceEntity> findAllByCustomerIdAndStatusNotInOrderByStartTimeAsc(int customerId, List<Status> status);

    Page<InsuranceEntity> findAllByEndCoverageBeforeOrderByEndCoverageAsc(LocalDate end,Pageable pageable);

    Page<InsuranceEntity> findAllByStatus(Status status, Pageable pageable);

    Page<InsuranceEntity> findAllByEndCoverageIsBefore(LocalDate date, Pageable pageable);

    List<InsuranceEntity> findAllByStatusIn(List<Status> status);

    List<InsuranceEntity> findAllByCustomerId(int customerId);

    @Query(value = "select customer.id as customerId, status as status, count(*) as count from InsuranceEntity group by customer.id, status")
    List<CountByStatus> countByStatus();

    interface CountByStatus {
        Integer getCustomerId();

        Status getStatus();

        Long getCount();


    }

    @Query(countQuery = "select count(*) from InsuranceEntity i join i.insuredGood ig join i.customer c where i.status = :status and " +
            "            (ig.reference like :search or c.cf like :search or c.firstName like :search or c.lastName like :search or c.companyName like :search or c.lastName like :search )",
            value = "select i from InsuranceEntity i join i.insuredGood ig join i.customer c where  i.status = :status and" +
            " (ig.reference like :search or c.cf like :search or c.firstName like :search or c.lastName like :search or c.companyName like :search or c.lastName like :search )")
    Page<InsuranceEntity> search(@Param("status") Status status,@Param("search") String q, Pageable pageable);

}
