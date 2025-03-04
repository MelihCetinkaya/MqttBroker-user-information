package com.springexample.interuserinformationapp.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RecordValues {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ValueType valueType;

    private Double value;

}
