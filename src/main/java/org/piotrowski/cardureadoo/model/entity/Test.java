package org.piotrowski.cardureadoo.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.io.Serializable;

@Entity
public class Test implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "test")
    private String test;

}
