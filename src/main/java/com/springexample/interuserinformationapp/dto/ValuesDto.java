package com.springexample.interuserinformationapp.dto;


import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;

@Data
@Embeddable
public class ValuesDto {

    private Double temperature;
    private Double humidity;

}
