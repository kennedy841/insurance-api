package it.snapoli.services.insurance.payments;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsurancePaymentRepository extends JpaRepository<InsurancePayment, Integer> {

    Page<InsurancePayment> findAllByInsuranceId(int id);
}
