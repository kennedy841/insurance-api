package it.snapoli.services.insurance.insurance;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "insurance_company")
public class InsuranceCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
}
