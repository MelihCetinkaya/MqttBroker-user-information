package com.springexample.interuserinformationapp.repository;

import com.springexample.interuserinformationapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

    @Query("SELECT u FROM User u " +
            "WHERE u.values.temperature = (SELECT MAX(u2.values.temperature) FROM User u2)")
    User findUserRecordHeat();

    @Query("SELECT u FROM User u " +
            "WHERE u.values.humidity = (SELECT MAX(u2.values.humidity) FROM User u2)")
    User findUserRecordHumidity();

    User findUserByUsername(String username);

}
