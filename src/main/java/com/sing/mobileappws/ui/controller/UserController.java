package com.sing.mobileappws.ui.controller;

import com.sing.mobileappws.service.UserDto;
import com.sing.mobileappws.service.UserService;
import com.sing.mobileappws.ui.model.request.UserDetailsRequestModel;
import com.sing.mobileappws.ui.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping
    public String getUser(){
        return "Get";
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetailsRequestModel){
        UserRest response = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        UserDto createUser = userService.createUser(userDto);

        BeanUtils.copyProperties(createUser, response);

        return response;
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
