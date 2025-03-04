package com.springexample.interuserinformationapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;

    private String password;

    private String name;

    private String email;

    @Embedded
    private Values values = new Values();

    private Date date;
}
