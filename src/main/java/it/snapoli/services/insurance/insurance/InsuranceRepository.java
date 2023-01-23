package it.snapoli.services.insurance.insurance;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsuranceRepository extends JpaRepository<InsuranceEntity, Integer> {

    Page<InsuranceEntity> findAllByCustomerIdAndStatusNotInOrderByEndTimeAsc(int customerId, List<InsuranceEntity.Status> status, Pageable pageable);


    Page<InsuranceEntity> findAllByStatus(InsuranceEntity.Status status, Pageable pageable);
}
