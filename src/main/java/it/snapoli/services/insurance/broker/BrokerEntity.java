package it.snapoli.services.insurance.broker;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "broker")
public class BrokerEntity {

    @Id
    @GeneratedValue
    private int id;

    private String firstName;

    private String lastName;

}
