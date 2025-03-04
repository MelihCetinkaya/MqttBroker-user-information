package com.springexample.interuserinformationapp.repository;

import com.springexample.interuserinformationapp.entity.RecordValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepo extends JpaRepository<RecordValues, Integer> {


}
