package it.snapoli.services.insurance.notes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotesEntity {

    @Id
    @GeneratedValue
    private int id;
    private String operator;
    private String text;

    private LocalDateTime dateTime;

    private String referenceEntity;

    private String referenceEntityType;
}
