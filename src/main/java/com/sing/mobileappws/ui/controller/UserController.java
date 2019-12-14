package com.sing.mobileappws.ui.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @GetMapping
    public String getUser(){
        return "Get";
    }

    @PostMapping
    public String createUser(){
        return "post";
    }

    @PutMapping
    public String updateUser(){
        return "put";
    }

    @DeleteMapping
    public String deleteUser(){
        return "Delete";
    }
}
