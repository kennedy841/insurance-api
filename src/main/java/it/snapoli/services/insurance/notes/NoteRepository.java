package it.snapoli.services.insurance.notes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<NotesEntity, Integer> {

    public Page<NotesEntity> findAllByReferenceEntityTypeAndReferenceEntityOrderByIdDesc(String type, String entity, Pageable pageable);
}
