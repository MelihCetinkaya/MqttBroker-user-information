package com.springexample.interuserinformationapp.controller;


import com.springexample.interuserinformationapp.entity.User;
import com.springexample.interuserinformationapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserApi {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<Optional<User>> getUserProfile(@RequestParam int id) {
    return ResponseEntity.ok(userService.getUserProfile(id));
    }
}
