package it.snapoli.services.insurance.documents;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "document_part")
public class DocumentPartEntity {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    private String name;

    @Lob
    private String content;

    /**
     *     @Id
     *     @GeneratedValue
     *     private int id;
     *
     *     @NotNull
     *     private String name;
     *
     *     @Lob
     *     private String content;
     */

}
