package it.snapoli.services.insurance.documents;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsRepository extends JpaRepository<DocumentEntity, Integer> {

    public Page<DocumentEntity> findAllByCustomerId(int id, PageRequest pageRequest);

    public Page<DocumentEntity> findAllByInsuranceId(int id, PageRequest pageRequest);
}
