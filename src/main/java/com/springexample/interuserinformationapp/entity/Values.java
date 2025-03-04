package com.springexample.interuserinformationapp.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Embeddable

public class Values {
    private Double temperature ;
    private Double humidity ;
}

