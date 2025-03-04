package com.springexample.interuserinformationapp.dto;

import jakarta.persistence.Embedded;
import lombok.Data;

import java.util.Date;

@Data
public class UserRegister {

    private String username;
    private String password;
    private String email;
    private String name;

    @Embedded
    private ValuesDto values;

}
