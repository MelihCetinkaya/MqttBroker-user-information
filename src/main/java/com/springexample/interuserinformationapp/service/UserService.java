package com.springexample.interuserinformationapp.service;

import com.springexample.interuserinformationapp.dto.UserInfos;
import com.springexample.interuserinformationapp.dto.UserLogin;
import com.springexample.interuserinformationapp.dto.UserRegister;
import com.springexample.interuserinformationapp.entity.User;
import com.springexample.interuserinformationapp.entity.Values;
import com.springexample.interuserinformationapp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepo userRepo;
    private final MqttService mqttService; // changeable

    public Optional<User> getUserProfile(int id) {
        return userRepo.findById(id);
    }

    public boolean userAuth(UserLogin userLogin) {

        User user=userRepo.findUserByUsername(userLogin.getUsername());

        if(Objects.equals(user.getPassword(), userLogin.getPassword())){

         System.out.println("login success");
         mqttService.init(userLogin.getUsername());

            return true;
        }
        return false;
    }

    public void userRegister(UserRegister userRegister) {

        User user = new User();
        user.setName(userRegister.getName());
        user.setPassword(userRegister.getPassword());
        user.setEmail(userRegister.getEmail());
        user.setUsername(userRegister.getUsername());

        Values values = new Values();

        values.setHumidity(0.0);
        values.setTemperature(0.0);


        user.setValues(values);

        userRepo.save(user);

    }
}
