package com.springexample.interuserinformationapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class InterUserInformationApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterUserInformationApplication.class, args);
    }

}
