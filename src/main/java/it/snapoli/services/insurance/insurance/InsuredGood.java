package it.snapoli.services.insurance.insurance;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "insured_product")
public class InsuredGood {

    @Id
    @GeneratedValue
    private int id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String note;

    private String reference;

    private String name;


    enum Type{
        car,motorcycle,truck,trailer, person, good,motorcar
    }
}
