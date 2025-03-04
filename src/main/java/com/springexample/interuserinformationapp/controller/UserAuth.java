package com.springexample.interuserinformationapp.controller;

import com.springexample.interuserinformationapp.dto.UserLogin;
import com.springexample.interuserinformationapp.dto.UserRegister;
import com.springexample.interuserinformationapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuth {

    private final UserService userService;

    @PostMapping("/login")
    public Boolean userAuth(@RequestBody UserLogin userLogin) {

        return userService.userAuth(userLogin);
    }

    @PostMapping("/register")
    public void userRegister(@RequestBody UserRegister userRegister) {

         userService.userRegister(userRegister);

    }
}
